package com.CFC.pennywizeapp

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import com.CFC.pennywizeapp.BuildConfig


object SupabaseClient {


    val instance: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth)      // Now this uses Supabase's install, not Ktor's
            install(Postgrest)
        }
    }
}