package com.CFC.pennywizeapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.CFC.pennywizeapp.viewmodel.AuthViewModel

@Composable
fun SignupScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Green theme colors
    val greenPrimary = Color(0xFF2E7D32)
    val greenLight = Color(0xFF4CAF50)
    val greenContainer = Color(0xFFE8F5E9)

    // Clear errors when typing
    LaunchedEffect(email, password, confirmPassword) {
        authViewModel.clearError()
        passwordError = null
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo - Squircle with no background
            AppLogo(
                modifier = Modifier.size(100.dp),
                cornerRadius = 20
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = greenPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign up to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("user@example.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = greenPrimary,
                    unfocusedBorderColor = greenPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = greenPrimary,
                    cursorColor = greenPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = greenPrimary,
                    unfocusedBorderColor = greenPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = greenPrimary,
                    cursorColor = greenPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = passwordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = greenPrimary,
                    unfocusedBorderColor = greenPrimary.copy(alpha = 0.5f),
                    focusedLabelColor = greenPrimary,
                    cursorColor = greenPrimary,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            )

            if (passwordError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = passwordError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Sign Up Button
            Button(
                onClick = {
                    when {
                        password != confirmPassword -> {
                            passwordError = "Passwords do not match"
                        }
                        password.length < 6 -> {
                            passwordError = "Password must be at least 6 characters"
                        }
                        email.isBlank() -> {
                            authViewModel.clearError()
                            passwordError = null
                        }
                        else -> {
                            authViewModel.signUp(email, password) {
                                navController.navigate("login") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = greenPrimary,
                    disabledContainerColor = greenPrimary.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign In link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                TextButton(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = greenPrimary
                    )
                ) {
                    Text(
                        text = "Sign In",
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Error/Success message
            if (error != null && error!!.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (error!!.contains("success", ignoreCase = true))
                            greenContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = error!!,
                        color = if (error!!.contains("success", ignoreCase = true))
                            greenPrimary
                        else
                            MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}