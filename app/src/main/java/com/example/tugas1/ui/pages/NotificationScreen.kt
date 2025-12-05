package com.example.tugas1.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class NotificationItem(
    val title: String,
    val message: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {

    val notifications = listOf(
        NotificationItem("Order Shipped", "Your order #1234 has been shipped"),
        NotificationItem("New Discount", "Get 20% off on your next purchase"),
        NotificationItem("Message from Seller", "Please check the details of your order")
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notifications") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(notifications) { notification ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(notification.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(notification.message, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
