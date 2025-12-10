package com.example.tugas1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.tugas1.ui.LoginScreen
import com.example.tugas1.ui.RegisterScreen
import com.example.tugas1.ui.nav.AppBottomNavigation
import com.example.tugas1.ui.pages.*
import com.example.tugas1.viewmodel.AuthViewModel
import com.example.tugas1.viewmodel.ProductViewModel
import com.example.tugas1.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val isAuthenticated by authViewModel.authState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("dashboard", "wishlist", "chat", "profile")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigation(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            authViewModel = authViewModel,
            profileViewModel = profileViewModel
        )
    }

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            // Memanggil nama fungsi yang benar: loadProfile
            profileViewModel.loadProfile()
            if (navController.currentDestination?.parent?.route != "main_graph") {
                navController.navigate("main_graph") { popUpTo(0) }
            }
        } else {
            // Memanggil nama fungsi yang benar: clearProfile
            profileViewModel.clearProfile()
            if (navController.currentDestination?.parent?.route != "auth_graph") {
                navController.navigate("auth_graph") { popUpTo(0) }
            }
        }
    }
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: androidx.navigation.NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel
) {
    val productViewModel: ProductViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "auth_graph",
        modifier = modifier
    ) {
        navigation(startDestination = "login", route = "auth_graph") {
            composable("login") { LoginScreen(navController, authViewModel) }
            composable("register") { RegisterScreen(navController, authViewModel) }
        }

        navigation(startDestination = "dashboard", route = "main_graph") {
            composable("dashboard") { DashboardScreen(navController, productViewModel) }
            composable("wishlist") { /* ... */ }
            composable("chat") { /* ... */ }

            composable("profile") {
                // Sekarang kita memberikan parameter onLogout, dan tidak lagi memberikan authViewModel
                ProfileScreen(
                    navController = navController,
                    profileViewModel = profileViewModel,
                    onLogout = { authViewModel.logout() } // Aksi logout diteruskan dari sini
                )
            }

            composable("cart") { CartScreen(navController, productViewModel) }
            composable("checkout") { /* ... */ }

            composable("edit_profile") {
                EditProfileScreen(
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }
            composable("productDetail/{productId}") { /* ... */ }
        }
    }
}
