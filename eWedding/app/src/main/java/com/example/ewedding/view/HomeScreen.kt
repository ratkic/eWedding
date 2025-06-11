package com.example.ewedding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ewedding.R
import com.example.ewedding.viewmodel.HomeViewModel
import com.example.ewedding.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import com.example.ewedding.viewmodel.GuestListViewModel
import androidx.compose.material3.Text
import com.example.ewedding.ui.theme.FontMontserrat
import com.example.ewedding.ui.theme.FontPlayfair
import com.example.ewedding.ui.theme.FontPoppins

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel(), authViewModel: AuthViewModel = viewModel(), guestListViewModel: GuestListViewModel = viewModel(),
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var weddingDate by remember { mutableStateOf<Date?>(null) }
    val guestList by guestListViewModel.guestList.collectAsState()


    LaunchedEffect(userId) {
        userId?.let {
            viewModel.getWeddingDate(it) { date ->
                weddingDate = date
            }
            guestListViewModel.fetchGuests(it) // Dohvati goste
        }
    }

    val currentDate = Calendar.getInstance().time
    val diffInMillis = weddingDate?.time?.minus(currentDate.time) ?: 0L
    val daysLeft = abs(diffInMillis / (24 * 60 * 60 * 1000))

    val totalGuests = guestList.size

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Logo i Logout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )

                Button(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("HomeScreen") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB28D4F))
                ) {
                    Text("Logout", color = Color.White, fontFamily = FontPoppins)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wedding Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBE7))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Wedding day", style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp), fontFamily = FontPlayfair, color = Color(0xFF7C4B00))

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow(
                        icon = R.drawable.calendar,
                        text = weddingDate?.let { SimpleDateFormat("dd/MM/yyyy").format(it) }
                            ?: "Not set"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow(
                        icon = R.drawable.clock,
                        text = "$daysLeft days until wedding",
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow(
                        icon = R.drawable.guests,
                        text = "$totalGuests guests"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Button Section
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NavigationButton("Guest List", R.drawable.checklist) {
                    navController.navigate("GuestListScreen")
                }

                NavigationButton("Budget", R.drawable.budget) {
                    navController.navigate("BudgetScreen")
                }

                NavigationButton("Playlist", R.drawable.playlist) {
                    navController.navigate("PlaylistScreen")
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.Black, fontFamily = FontMontserrat)
    }
}

@Composable
fun NavigationButton(text: String, iconId: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4B00)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.White, fontFamily = FontPoppins)
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = NavController(context = LocalContext.current))
}
