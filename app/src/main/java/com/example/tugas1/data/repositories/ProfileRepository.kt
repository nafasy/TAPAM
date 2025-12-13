package com.example.tugas1.data.repositories

import com.example.tugas1.model.Profile
import com.example.tugas1.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ProfileRepository {

    // Mendapatkan instance client Supabase dari objek SupabaseClient Anda
    private val client = SupabaseClient.client

    // Mendapatkan modul Postgrest untuk interaksi database
    private val postgrest = client.postgrest

    // Mendapatkan modul Storage untuk upload file
    private val storage = client.storage

    suspend fun getProfile(userId: String): Profile? {
        return try {
            postgrest.from("profiles") // Nama tabel di Supabase
                .select {
                    filter {
                        eq("id", userId) // Filter dimana kolom 'id' sama dengan userId
                    }
                }
                .decodeSingleOrNull<Profile>() // Ubah hasil JSON ke objek Profile?
        } catch (e: Exception) {
            // Cetak error ke logcat untuk debugging
            println("Error getting profile: ${e.message}")
            null // Kembalikan null jika terjadi kesalahan
        }
    }

    suspend fun updateProfile(id: String, newName: String, newUsername: String) {
        try {
            postgrest.from("profiles").update(
                // Data yang akan diubah, dalam format JSON
                buildJsonObject {
                    put("full_name", newName)
                    put("username", newUsername)
                }
            ) {
                // Kondisi WHERE untuk pembaruan
                filter {
                    eq("id", id)
                }
            }
        } catch (e: Exception) {
            println("Error updating profile: ${e.message}")
        }
    }

    /**
     * Mengunggah gambar avatar, mendapatkan URL publiknya, dan menyimpannya di tabel profil.
     * @param userId ID pengguna yang sedang login.
     * @param avatarData ByteArray dari file gambar.
     * @return URL publik dari gambar yang baru diunggah, atau null jika gagal.
     */
    suspend fun uploadAvatar(userId: String, avatarData: ByteArray): String? {
        return try {
            // Buat path yang unik untuk file di storage, contoh: "public/UUID_user/avatar.png"
            // Ini mencegah file saling menimpa.
            val filePath = "public/${userId}/avatar.png"

            // 1. Upload (atau timpa jika sudah ada) gambar ke bucket 'avatars'
            storage.from("avatars").upload(
                path = filePath,
                data = avatarData,
                upsert = true // `upsert = true` berarti akan menimpa file jika ada
            )

            // 2. Dapatkan URL publik dari gambar yang baru di-upload
            val newAvatarUrl = storage.from("avatars").publicUrl(path = filePath)

            // 3. Update kolom 'avatar_url' di tabel 'profiles' dengan URL baru
            postgrest.from("profiles").update(
                buildJsonObject {
                    put("avatar_url", newAvatarUrl)
                }
            ) {
                filter {
                    eq("id", userId)
                }
            }

            // Kembalikan URL baru untuk langsung ditampilkan di UI
            newAvatarUrl
        } catch (e: Exception) {
            println("Error uploading avatar: ${e.message}")
            null
        }
    }

    /**
     * Membuat profil baru. Biasanya dipanggil setelah pengguna berhasil mendaftar.
     * @param profile Objek Profile yang berisi data awal.
     */
    suspend fun createProfile(profile: Profile) {
        try {
            postgrest.from("profiles").insert(profile)
        } catch (e: Exception) {
            println("Error creating profile: ${e.message}")
        }
    }
}
