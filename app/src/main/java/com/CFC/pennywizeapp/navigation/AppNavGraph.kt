package com.CFC.pennywizeapp.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.CFC.pennywizeapp.ui.LoginScreen
import com.CFC.pennywizeapp.ui.SignupScreen
import com.CFC.pennywizeapp.viewmodel.AuthViewModel

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

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