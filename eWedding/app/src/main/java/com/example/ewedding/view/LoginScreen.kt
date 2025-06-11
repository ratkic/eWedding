package com.example.ewedding.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ewedding.viewmodel.AuthViewModel
import com.example.ewedding.R
import androidx.compose.material3.TextFieldDefaults
import com.example.ewedding.ui.theme.FontPlayfair
import com.example.ewedding.ui.theme.FontPoppins
import com.google.firebase.auth.FirebaseAuth


@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            // LOGO - top left
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(16.dp)
                    .size(120.dp)
                    .align(Alignment.TopStart)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Text("Welcome to eWedding", fontSize = 22.sp, fontFamily = FontPlayfair, color = Color(0xFF7C4B00))

                Spacer(modifier = Modifier.height(24.dp))

                // Email Input
                EmailPasswordField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password Input
                EmailPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    isPassword = true
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error ?: "", color = Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Don’t have an account? ", color = Color.DarkGray)
                    Text("Create new.", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            error = "Email and password cannot be empty."
                        } else {
                            isLoading = true
                            viewModel.login(email, password) { success, errorMessage ->
                                isLoading = false
                                if (success) {
                                    error = null
                                    Log.d("LoginScreen", "Login successful, navigating to HomeScreen.")
                                    // provjera je li korisnik uspješno prijavljen
                                    if (FirebaseAuth.getInstance().currentUser != null) {
                                        navController.navigate("HomeScreen") {
                                            popUpTo("login") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    } else {
                                        Log.e("LoginScreen", "User not authenticated in Firebase.")
                                    }
                                } else {
                                    error = errorMessage ?: "Login failed."
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB28D4F)),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Login", color = Color.White, fontFamily = FontPoppins)
                }

            }
        }
    }
}

@Composable
fun EmailPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 4.dp,
        color = Color(0xFFFAF5F1)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}
