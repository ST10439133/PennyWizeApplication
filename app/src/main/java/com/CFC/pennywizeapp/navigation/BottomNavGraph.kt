package com.CFC.pennywizeapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.CFC.pennywizeapp.ui.screens.*
import com.CFC.pennywizeapp.viewmodel.AuthViewModel
import com.CFC.pennywizeapp.viewmodel.EntryViewModel

@Composable
fun BottomNavGraph(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel
) {

    val navController = rememberNavController()

    val entryViewModel: EntryViewModel = viewModel()
    val categories by entryViewModel.categories.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentRoute == Routes.Budget,
                    onClick = {
                        navController.navigate(Routes.Budget) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.DateRange, null) },
                    label = { Text("Budget") }
                )

                NavigationBarItem(
                    selected = currentRoute == Routes.Add,
                    onClick = {
                        navController.navigate(Routes.Add)
                    },
                    icon = { Icon(Icons.Default.Add, null) },
                    label = { Text("Add") }
                )

                NavigationBarItem(
                    selected = currentRoute == Routes.History,
                    onClick = {
                        navController.navigate(Routes.History)
                    },
                    icon = { Icon(Icons.Default.List, null) },
                    label = { Text("History") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        authViewModel.signOut {
                            rootNavController.navigate(Routes.Login) {
                                popUpTo(0)
                            }
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                    label = { Text("Logout") }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.Budget,
            modifier = Modifier.padding(padding)
        ) {

            composable(Routes.Budget) {
                BudgetGoalsScreen(
                    onNavigateToAdd = {
                        navController.navigate(Routes.Add)
                    }
                )
            }

            composable(Routes.Add) {
                AddEntryScreen(
                    categories = categories,
                    onSave = { entry ->
                        entryViewModel.insertEntry(entry)

                        navController.navigate(Routes.History) {
                            popUpTo(Routes.Add) { inclusive = true }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.History) {
                ExpenseListScreen()
            }
        }
    }
}