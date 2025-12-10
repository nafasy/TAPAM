package com.example.tugas1.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tugas1.R
import com.example.tugas1.viewmodel.ProductViewModel

// Data class untuk merepresentasikan produk
data class Product(
    val name: String,
    val category: String,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, productViewModel: ProductViewModel) {
    // SOLUSI: Gunakan Scaffold untuk struktur layar yang benar.
    Scaffold(
        // Tempatkan TopBarContent di dalam slot `topBar` agar posisinya tetap di atas.
        topBar = {
            TopBarContent(
                onCartClick = { navController.navigate("cart") }
            )
        }
        // Jika nanti Anda memiliki bottom navigation, Anda bisa menambahkannya di sini:
        // bottomBar = { AppBottomNavigation(...) }
    ) { innerPadding ->
        // `innerPadding` adalah padding yang disediakan oleh Scaffold untuk memastikan
        // konten tidak tumpang tindih dengan topBar atau bottomBar.

        // Tempatkan konten utama layar di sini.
        DashboardContent(
            // Terapkan padding dari Scaffold ke modifier konten.
            modifier = Modifier.padding(innerPadding),
            onProductClick = { product ->
                // TODO: Navigasi ke halaman detail produk
                // Contoh: navController.navigate("productDetail/${product.name}")
            }
        )
    }
}

// --- COMPOSABLE UNTUK BAGIAN ATAS (TOP BAR) ---
@Composable
fun TopBarContent(onCartClick: () -> Unit) {
    // Tidak ada perubahan di sini, sudah bagus.
    // Menambahkan background untuk memastikan warnanya konsisten jika tema berubah.
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp // Optional: Menambah bayangan tipis di bawah top bar
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // Gunakan warna dari tema
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "H&M Logo",
                modifier = Modifier.height(35.dp),
                contentScale = ContentScale.Fit
            )
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Open Cart"
                )
            }
        }
    }
}


// --- COMPOSABLE UNTUK KONTEN UTAMA ---
@Composable
fun DashboardContent(modifier: Modifier = Modifier, onProductClick: (Product) -> Unit) {
    // Tidak ada perubahan besar di sini, hanya menerima modifier dari parent.
    val newArrivals = listOf(
        Product("Succulent Plant", "PLANTS", "https://i.imgur.com/gX2L9aJ.png"),
        Product("Mobile Lens", "GEAR", "https://i.imgur.com/9vL2dF6.png"),
        Product("Yellow Letter", "DECOR", "https://i.imgur.com/uC5G1mN.png"),
        Product("Little Reader", "FIGURES", "https://i.imgur.com/V28p8aM.png"),
        Product("Yellow Headphones", "GEAR", "https://i.imgur.com/O6aJ3O7.png"),
        Product("Blue Speaker", "GEAR", "https://i.imgur.com/E8w9o4c.png"),
        Product("Colorful Pillows", "DECOR", "https://i.imgur.com/Yh7WfA9.png"),
        Product("Coffee Cup", "HOME", "https://i.imgur.com/5V3X4h9.png"),
        Product("Toy Car", "TOYS", "https://i.imgur.com/3f0i5vS.png")
    )

    LazyColumn(
        modifier = modifier, // Modifier ini sekarang berisi padding dari Scaffold
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp), // Menambah sedikit padding atas
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(onClick = { /*TODO*/ }) {
                    Text("READ MORE")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NEW ARRIVALS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(((newArrivals.size + 1) / 2 * 280).dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                userScrollEnabled = false
            ) {
                items(newArrivals) { product ->
                    ProductCard(product = product, onClick = { onProductClick(product) })
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "SAVE UP TO 30%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = { /*TODO*/ }) {
                    Text("READ MORE")
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    // Tidak ada perubahan di sini, sudah bagus.
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.imageUrl),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.category,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
