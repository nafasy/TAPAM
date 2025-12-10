package com.example.tugas1.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// 1. Data class untuk merepresentasikan setiap item di Bottom Navigation Bar
data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

// 2. Composable untuk Bottom Navigation Bar yang bisa digunakan di mana saja
@Composable
fun AppBottomNavigation(navController: NavController) {
    // Daftar item navigasi
    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, "dashboard"),
        BottomNavItem("Wishlist", Icons.Default.Favorite, "wishlist"),
        // Anda bisa menambahkan "Cart" di sini jika mau, atau membiarkannya
        // BottomNavItem("Cart", Icons.Default.ShoppingCart, "cart"),
        BottomNavItem("Chat", Icons.Default.Chat, "chat"),
        // "Checkout" biasanya tidak ada di bottom nav, tapi tergantung desain Anda
        BottomNavItem("Checkout", Icons.Default.Check, "checkout"),
        BottomNavItem("Profile", Icons.Default.Person, "profile")
    )

    // State untuk mengetahui route yang sedang aktif
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                // Menandai item yang aktif
                selected = currentRoute == item.route,
                // Aksi ketika item diklik
                onClick = {
                    navController.navigate(item.route) {
                        // Logika ini mencegah tumpukan navigasi yang besar
                        // dengan me-launch tujuan di puncak back stack.
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                // Ikon untuk item
                icon = { Icon(item.icon, contentDescription = item.title) },
                // Label teks untuk item
                label = { Text(item.title) }
            )
        }
    }
}
