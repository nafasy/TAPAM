package com.example.tugas1.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tugas1.viewmodel.ProductViewModel

data class CartItem(
    val name: String,
    val price: Int
)

@Composable
fun CartScreen(navController: NavHostController, productViewModel: ProductViewModel) {

    val cartItems = listOf(
        CartItem("Handbag Luxury", 89),
        CartItem("Sport Shoes", 120),
        CartItem("Beauty Kit", 49)
    )

    val totalPrice = cartItems.sumOf { it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "My Cart",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ===================
        // CART LIST
        // ===================
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(cartItems) { item ->
                CartItemCardSimple(item)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // ===================
        // TOTAL SECTION
        // ===================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total", style = MaterialTheme.typography.bodyLarge)
            Text("$${totalPrice}", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { /* Checkout */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Checkout")
        }
    }
}

@Composable
fun CartItemCardSimple(item: CartItem) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder box sebagai "image"
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(item.name)
            Text("$${item.price}", color = Color.Black)
        }
    }
}
