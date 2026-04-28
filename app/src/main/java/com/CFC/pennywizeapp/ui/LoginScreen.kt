package com.CFC.pennywizeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.CFC.pennywizeapp.navigation.Routes
import com.CFC.pennywizeapp.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()

    val greenPrimary = Color(0xFF2E7D32)
    val greenLight = Color(0xFF4CAF50)

    LaunchedEffect(email, password) {
        authViewModel.clearError()
    }

    Scaffold(containerColor = Color.White) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ✅ LOGO (same as signup)
            AppLogo(
                modifier = Modifier.size(100.dp),
                cornerRadius = 20
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = greenPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to continue",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = greenPrimary,
                    unfocusedBorderColor = greenPrimary.copy(alpha = 0.5f),
                    cursorColor = greenPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = greenPrimary,
                    unfocusedBorderColor = greenPrimary.copy(alpha = 0.5f),
                    cursorColor = greenPrimary
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // SIGN IN BUTTON (same logic)
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        authViewModel.signIn(email, password) {
                            onLoginSuccess()
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = greenPrimary
                )
            ) {
                Text("Sign In")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SIGN UP NAV
            TextButton(
                onClick = { navController.navigate(Routes.Signup) }
            ) {
                Text("Don't have an account? Sign Up", color = greenPrimary)
            }

            // ERROR
            if (!error.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(error ?: "", color = Color.Red)
            }
        }
    }
}