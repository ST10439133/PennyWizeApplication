package com.CFC.pennywizeapp.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.CFC.pennywizeapp.data.EntryRepository
import com.CFC.pennywizeapp.ui.LoginScreen
import com.CFC.pennywizeapp.ui.SignupScreen
import com.CFC.pennywizeapp.viewmodel.AuthViewModel

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    val context = LocalContext.current

    // Initialize repositories
    val entryRepository = EntryRepository.getInstance(context)

    // Create AuthViewModel with entryRepository dependency
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(entryRepository)
    )

    LaunchedEffect(Unit) {
        authViewModel.checkAuthStatus()
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Login
    ) {

        composable(Routes.Login) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Main) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Signup) {
            SignupScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Routes.Main) {
            BottomNavGraph(
                rootNavController = navController,
                authViewModel = authViewModel
            )
        }
    }
}

// AuthViewModel Factory
class AuthViewModelFactory(
    private val entryRepository: EntryRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AuthViewModel(entryRepository) as T
    }
}