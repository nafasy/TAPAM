package com.example.tugas1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugas1.data.remote.SupabaseClient
import com.example.tugas1.model.Profile
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ProfileViewModel : ViewModel() {

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        getProfile()
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    fun getProfile() {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                // 1. Mendapatkan objek user saat ini. Jika null, lempar exception.
                val user = SupabaseClient.client.auth.currentUserOrNull()
                    ?: throw IllegalStateException("Pengguna tidak login")

                val userId = user.id
                // Menggunakan email untuk inisiasi otomatis
                val userEmail = user.email ?: "username_baru@example.com"

                // 2. Cek apakah profil sudah ada di database berdasarkan userId
                val profileData: Profile? = SupabaseClient.client.postgrest
                    .from("profiles")
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull()

                // 3. Jika profil belum ada (pertama kali login), buat profil baru
                if (profileData == null) {

                    val emailPrefix = userEmail.substringBefore('@')

                    val defaultName = emailPrefix
                        .replace('.', ' ')
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                    val defaultUsername = emailPrefix.replace('.', '_')

                    val newProfile = Profile(
                        id = userId,
                        fullName = defaultName,
                        username = defaultUsername,
                        avatarUrl = null
                    )

                    // Simpan profil default ke database
                    SupabaseClient.client.postgrest.from("profiles").insert(newProfile)

                    _profile.value = newProfile
                } else {
                    // 🛑 Jika profil sudah ada, TAMPILKAN DATA YANG SUDAH ADA.
                    _profile.value = profileData
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengambil profil: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateProfile(fullName: String, username: String) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                // Pastikan user sedang login
                val user = SupabaseClient.client.auth.currentUserOrNull() ?: return@launch

                if (username.isBlank()) {
                    _errorMessage.value = "Username tidak boleh kosong."
                    _loading.value = false
                    return@launch
                }

                val updates = mapOf(
                    // 🛑 PASTIKAN NAMA KEY INI SAMA DENGAN NAMA KOLOM DI SUPABASE
                    "full_name" to fullName,
                    "username" to username
                )

                // 🛑 UPDATE DATA DI BARIS YANG ID-NYA COCOK DENGAN ID USER
                SupabaseClient.client.postgrest.from("profiles").update(updates) {
                    filter {
                        eq("id", user.id) // Ini memastikan hanya baris user yang sedang login yang diupdate
                    }
                }

                getProfile() // Muat ulang data setelah update berhasil
            } catch (e: Exception) {
                // Jika update gagal (seringkali karena RLS), error akan ditampilkan
                _errorMessage.value = "Gagal memperbarui profil: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun uploadAvatar(imageBytes: ByteArray) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                val userId = SupabaseClient.client.auth.currentUserOrNull() ?: return@launch

                val filePath = "${userId.id}/avatar.png"

                SupabaseClient.client.storage.from("avatars").upload(
                    path = filePath,
                    data = imageBytes,
                    upsert = true
                )

                val updates = mapOf("avatar_url" to filePath)
                SupabaseClient.client.postgrest.from("profiles").update(updates) {
                    filter { eq("id", userId.id) }
                }

                getProfile()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengunggah avatar: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getAvatarPublicUrl(path: String): String {
        return SupabaseClient.client.storage.from("avatars").publicUrl(path)
    }
}