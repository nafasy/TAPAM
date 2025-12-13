package com.example.tugas1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugas1.data.remote.SupabaseClient
import com.example.tugas1.model.Profile
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest // <-- Import dasar untuk Postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ProfileViewModel : ViewModel() {
    // ... (StateFlow dan init tetap sama) ...

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        getProfile()
    }

    fun getProfile() {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
                    ?: throw IllegalStateException("Pengguna tidak login")

                val profileData: Profile? = SupabaseClient.client.postgrest
                    .from("profiles")
                    .select {
                        filter {
                            // Mengakses 'eq' sebagai ekstensi dari PostgrestFilterBuilder
                            // Ini adalah metode paling stabil.
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull()
                // ... (sisanya tetap sama) ...
                if (profileData == null) {
                    val newProfile = Profile(id = userId, fullName = "Nama Belum Diatur", username = "username")
                    SupabaseClient.client.postgrest.from("profiles").insert(newProfile)
                    _profile.value = newProfile
                } else {
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
                val userId = SupabaseClient.client.auth.currentUserOrNull() ?: return@launch

                if (username.isBlank()) {
                    _errorMessage.value = "Username tidak boleh kosong."
                    return@launch
                }

                val updates = mapOf(
                    "full_name" to fullName,
                    "username" to username
                )

                SupabaseClient.client.postgrest.from("profiles").update(updates) {
                    filter {
                        eq("id", userId.id) // Menggunakan eq
                    }
                }

                getProfile()
            } catch (e: Exception) {
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
                    filter { eq("id", userId.id) } // Menggunakan eq
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