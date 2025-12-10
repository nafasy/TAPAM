package com.example.tugas1.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.tugas1.viewmodel.ProductViewModel

// 1. Data class diperbarui dengan properti `isSelected`
data class CartItem(
    val name: String,
    val category: String,
    val color: String,
    val size: String,
    val originalPrice: Double,
    val discount: Double,
    val imageUrl: String,
    var quantity: Int,
    var isSelected: Boolean = true // Properti baru, default-nya terpilih
) {
    fun discountedPrice(): Double = originalPrice * (1 - discount / 100)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavHostController, productViewModel: ProductViewModel) {

    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItem("Delightful Honey - Brown Contact Lenses", "Canadian Footwear", "Blue, Brown", "6", 652.0, 20.0, "https://i.imgur.com/8oP4vFA.png", 1),
                CartItem("Acton Propulsion", "Canadian Footwear", "Blue, Brown", "6", 652.0, 20.0, "https://i.imgur.com/uR1G46x.png", 1),
                CartItem("Kodiak Trek", "Canadian Footwear", "Blue, Brown", "6", 652.0, 20.0, "https://i.imgur.com/uV23UjE.png", 1),
                CartItem("Terra Crossbow", "Canadian Footwear", "Blue, Brown", "6", 652.0, 20.0, "https://i.imgur.com/9O3tQfc.png", 1)
            )
        )
    }

    // Hitung total hanya untuk item yang 'isSelected'
    val totalPrice = cartItems.filter { it.isSelected }.sumOf { it.discountedPrice() * it.quantity }
    val selectedItemsCount = cartItems.count { it.isSelected }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart (${cartItems.size})") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tombol "Select All" / "Deselect All"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    val areAllSelected = cartItems.all { it.isSelected }
                    cartItems = cartItems.map { it.copy(isSelected = !areAllSelected) }
                }) {
                    Text(if (cartItems.all { it.isSelected }) "Deselect All" else "Select All")
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(cartItems, key = { _, item -> item.name /* Beri kunci unik */ }) { index, item ->
                    CartItemCard(
                        item = item,
                        onQuantityChange = { newQuantity ->
                            val updatedList = cartItems.toMutableList()
                            updatedList[index] = item.copy(quantity = newQuantity)
                            cartItems = updatedList
                        },
                        onRemoveClick = {
                            cartItems = cartItems.toMutableList().also { it.removeAt(index) }
                        },
                        onSelectionChange = { isSelected ->
                            val updatedList = cartItems.toMutableList()
                            updatedList[index] = item.copy(isSelected = isSelected)
                            cartItems = updatedList
                        }
                    )
                }
            }

            // Bagian Total dan Checkout
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total ($selectedItemsCount items):",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        "$${String.format("%.2f", totalPrice)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    // Tombol checkout hanya aktif jika ada item yang dipilih
                    enabled = selectedItemsCount > 0,
                    onClick = {
                        // TODO: Nanti, kirim hanya item yang terpilih ke ViewModel atau halaman checkout
                        // val itemsToCheckout = cartItems.filter { it.isSelected }
                        // productViewModel.setItemsForCheckout(itemsToCheckout)
                        navController.navigate("checkout")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Proceed to Checkout", fontSize = 16.sp)
                }
            }
        }
    }
}

// 2. Composable Card diperbarui dengan checkbox
@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (newQuantity: Int) -> Unit,
    onRemoveClick: () -> Unit,
    onSelectionChange: (isSelected: Boolean) -> Unit // Callback baru untuk status centang
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox ditambahkan di sini
            Checkbox(
                checked = item.isSelected,
                onCheckedChange = onSelectionChange,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            // Gambar Produk
            Image(
                painter = rememberAsyncImagePainter(model = item.imageUrl),
                contentDescription = item.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Kolom untuk semua teks dan tombol
            Column(modifier = Modifier.weight(1f)) {
                // ... (Sisa kode di dalam Column ini sama persis seperti sebelumnya)
                Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = item.category, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text("Color: ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(item.color, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Size: ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(item.size, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "$${String.format("%.2f", item.discountedPrice())}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$${String.format("%.2f", item.originalPrice)}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textDecoration = TextDecoration.LineThrough)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${item.discount.toInt()}% OFF", style = MaterialTheme.typography.bodyMedium, color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantitySelector(quantity = item.quantity, onQuantityChange = onQuantityChange)
                    TextButton(onClick = onRemoveClick) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remove")
                    }
                }
            }
        }
    }
}

// 3. Composable QuantitySelector (Tidak ada perubahan)
@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFE0E0E0), CircleShape)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
        }
        Text(
            text = "$quantity",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFE0E0E0), CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase quantity")
        }
    }
}
