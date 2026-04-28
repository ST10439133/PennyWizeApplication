package com.CFC.pennywizeapp

import android.app.Application
import com.CFC.pennywizeapp.data.BudgetRepository
import com.CFC.pennywizeapp.data.CategoryRepository
import com.CFC.pennywizeapp.data.EntryRepository

class PennyWizeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize all repositories
        EntryRepository.getInstance(this)
        CategoryRepository.getInstance(this)
        BudgetRepository.getInstance(this)  // ← ADD THIS

        println("PennyWizeApplication: All repositories initialized")
    }
}