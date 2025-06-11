package com.example.ewedding.view

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ewedding.viewmodel.AuthViewModel
import java.util.*
import com.example.ewedding.R
import com.example.ewedding.ui.theme.FontPlayfair
import com.example.ewedding.ui.theme.FontPoppins


@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var weddingDate by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        ShowDatePickerDialog(
            onDateSet = { selectedDate ->
                weddingDate = selectedDate
                showDatePicker = false
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // LOGO
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

                Text("Create your eWedding account", fontSize = 22.sp, fontFamily = FontPlayfair, color = Color(0xFF7C4B00))

                Spacer(modifier = Modifier.height(24.dp))

                EmailPasswordField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email"
                )

                Spacer(modifier = Modifier.height(12.dp))

                EmailPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAF5F1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    val text = weddingDate?.toString() ?: "Select Wedding Date"
                    Text(text, color = Color(0xFF7C4B00), fontFamily = FontPoppins)
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error ?: "", color = Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            error = "Email and password cannot be empty."
                        } else if (weddingDate == null) {
                            error = "Wedding date cannot be empty."
                        } else {
                            isLoading = true
                            authViewModel.register(email, password, weddingDate) { success, errorMessage ->
                                isLoading = false
                                if (success) {
                                    error = null
                                    navController.navigate("HomeScreen") {
                                        popUpTo("register") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                } else {
                                    error = errorMessage ?: "Registration failed."
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB28D4F)),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Register", color = Color.White, fontFamily = FontPoppins)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Already have an account? ", color = Color.DarkGray)
                    Text("Login", color = Color.Black)
                }
            }
        }
    }
}


@Composable
fun ShowDatePickerDialog(
    onDateSet: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            onDateSet(selectedDate.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.show()
}
