package com.CFC.pennywizeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CFC.pennywizeapp.theme.PennyWizeAppTheme
import com.CFC.pennywizeapp.ui.screens.AddEntryScreen
import com.CFC.pennywizeapp.ui.screens.BudgetGoalsScreen
import com.CFC.pennywizeapp.ui.screens.ExpenseListScreen
import com.CFC.pennywizeapp.viewmodel.EntryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PennyWizeAppTheme {
                MainNavigationContainer()
            }
        }
    }
}

@Composable
fun MainNavigationContainer() {
    // State to track which screen we are on
    var selectedTab by remember { mutableIntStateOf(0) }

    // ViewModels
    val entryViewModel: EntryViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Budget") },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Add") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("History") },
                    icon = { Icon(Icons.Default.List, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> BudgetGoalsScreen(
                    // Pass the navigation logic here
                    onNavigateToAdd = { selectedTab = 1 }
                )
                1 -> {
                    val categories by entryViewModel.categories.collectAsState()
                    AddEntryScreen(
                        categories = categories,
                        onSave = { entry ->
                            entryViewModel.insertEntry(entry)
                            selectedTab = 2 // Redirect to history after saving
                        },
                        onBack = { selectedTab = 0 }
                    )
                }
                2 -> ExpenseListScreen()
            }
        }
    }
}