package com.CFC.pennywizeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.CFC.pennywizeapp.theme.PennyWizeAppTheme
import com.CFC.pennywizeapp.ui.screens.AddEntryScreen
import com.CFC.pennywizeapp.viewmodel.EntryViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: EntryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Insert default categories ONCE
        //viewModel.insertDefaultCategories()

        setContent {
            val categories = viewModel.categories.collectAsState(initial = emptyList())
            AddEntryScreen(
                categories = categories.value,
                onSave = { entry ->
                    viewModel.insertEntry(entry)
                },
                onBack = {}
            )
        }
    }
}