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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.CFC.pennywizeapp.data.BudgetRepository
import com.CFC.pennywizeapp.data.CategoryRepository
import com.CFC.pennywizeapp.data.EntryRepository
import com.CFC.pennywizeapp.ui.screens.*
import com.CFC.pennywizeapp.viewmodel.AuthViewModel
import com.CFC.pennywizeapp.viewmodel.BudgetGoalsViewModel
import com.CFC.pennywizeapp.viewmodel.EntryViewModel
import com.CFC.pennywizeapp.viewmodel.ExpenseListViewModel

@Composable
fun BottomNavGraph(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel
) {

    val navController = rememberNavController()
    val context = LocalContext.current

    // Initialize repositories with RoomDB
    val entryRepository = EntryRepository.getInstance(context)
    val categoryRepository = CategoryRepository.getInstance(context)
    val budgetRepository = BudgetRepository.getInstance(context)  // ← ADD THIS

    // Create ViewModels with dependencies
    val entryViewModel: EntryViewModel = viewModel(
        factory = EntryViewModelFactory(entryRepository, categoryRepository)
    )

    val budgetGoalsViewModel: BudgetGoalsViewModel = viewModel(
        factory = BudgetGoalsViewModelFactory(entryRepository, budgetRepository)  // ← UPDATE
    )

    val expenseListViewModel: ExpenseListViewModel = viewModel(
        factory = ExpenseListViewModelFactory(entryRepository, categoryRepository)
    )

    val categories by entryViewModel.categories.collectAsState()

    // Get currentUserId as nullable String from StateFlow
    val currentUserIdState by authViewModel.currentUserId.collectAsState()
    val currentUserId = currentUserIdState

    // Only set user when ID is non-null and not empty
    LaunchedEffect(currentUserId) {
        if (currentUserId != null && currentUserId.isNotEmpty()) {
            entryRepository.setCurrentUser(currentUserId)
            budgetRepository.setCurrentUser(currentUserId)  // ← ADD THIS
        }
    }

    // Clean up on disposal
    DisposableEffect(Unit) {
        onDispose {
            entryRepository.clearCurrentUser()
            budgetRepository.clearCurrentUser()  // ← ADD THIS
        }
    }

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
                    viewModel = budgetGoalsViewModel,
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
                ExpenseListScreen(viewModel = expenseListViewModel)
            }
        }
    }
}

// ViewModel Factories
class EntryViewModelFactory(
    private val entryRepository: EntryRepository,
    private val categoryRepository: CategoryRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EntryViewModel(entryRepository, categoryRepository) as T
    }
}

class BudgetGoalsViewModelFactory(
    private val entryRepository: EntryRepository,
    private val budgetRepository: BudgetRepository  // ← ADD THIS
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return BudgetGoalsViewModel(entryRepository, budgetRepository) as T
    }
}

class ExpenseListViewModelFactory(
    private val entryRepository: EntryRepository,
    private val categoryRepository: CategoryRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ExpenseListViewModel(entryRepository, categoryRepository) as T
    }
}