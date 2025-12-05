/*
package com.example.tugas1.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseHolder {
    // Ganti dengan URL & anon/public key Supabase Anda
    private const val SUPABASE_URL = "https://yxwvgddrriwwdksymvre.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl4d3ZnZGRycml3d2Rrc3ltdnJlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE1MDU3ODQsImV4cCI6MjA3NzA4MTc4NH0.5kDQvpumBJ8BJySpwqtIl90J_1aWaBNlTz4QAsWRG74"


    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest.Companion)
        install(Storage.Companion)
    }

    fun session(): UserSession? = client.auth.currentSessionOrNull()
}*/
