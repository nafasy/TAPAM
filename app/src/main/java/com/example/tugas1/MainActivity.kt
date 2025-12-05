package com.example.tugas1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tugas1.ui.LoginScreen
import com.example.tugas1.ui.RegisterScreen
import com.example.tugas1.ui.pages.*

import com.example.tugas1.viewmodel.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val productViewModel: ProductViewModel = viewModel()

    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {

            // Login/Register
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }

            // Bottom Nav Screens (Home, Cart, Wishlist, Profile)
            composable("dashboard") { DashboardScreen(navController, productViewModel) }
            composable("cart") { CartScreen(navController, productViewModel) }
            composable("wishlist") { WishlistScreen(navController, productViewModel) }
            composable("profile") { ProfileScreen(navController) }
            composable("notification") { NotificationScreen(navController) }
            composable("chat") { ChatScreen(navController) }


            // Product Detail Screen
            composable(
                route = "productDetail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    navController = navController,
                    productViewModel = productViewModel,
                    productId = productId
                )
            }
        }
    }
}
