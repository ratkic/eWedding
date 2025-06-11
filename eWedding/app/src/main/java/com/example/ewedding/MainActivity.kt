package com.example.ewedding

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ewedding.view.LoginScreen
import com.example.ewedding.view.RegisterScreen
import com.example.ewedding.ui.theme.EWeddingTheme
import com.example.ewedding.view.BudgetScreen
import com.example.ewedding.view.GuestListScreen
import com.example.ewedding.view.HomeScreen
import com.example.ewedding.view.PlaylistScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EWeddingTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(navController = navController)
                        }
                        composable("register") {
                            RegisterScreen(navController = navController)
                        }
                        composable("HomeScreen") {
                            HomeScreen(navController = navController)
                        }
                        composable("GuestListScreen") {
                            GuestListScreen(navController = navController)
                        }
                        composable("BudgetScreen") {
                            BudgetScreen(navController = navController)
                        }
                        composable("PlaylistScreen") {
                            PlaylistScreen(navController = navController)
                        }
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

    }

}



