package com.example.tugas1.ui.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.tugas1.viewmodel.ProductViewModel

// --- Data class untuk metode pembayaran ---
data class PaymentMethod(
    val id: String,
    val name: String,
    val description: String
)

// --- UI State sederhana buat Checkout (kalau mau, bisa dipindah ke file sendiri) ---
data class CheckoutUiState(
    val selectedMethodId: String? = null,
    val paymentProofUrl: String? = null,
    val isUploading: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavHostController,
    productViewModel: ProductViewModel
) {
    // >>>>>>> Contoh: totalPrice kamu bisa kirim via navArgs atau ambil dari ViewModel
    // untuk demo ini, aku ambil dari fungsi di ViewModel (lihat bawah).
    val totalPrice by productViewModel.cartTotalPrice.collectAsState(0.0)

    // State Checkout dipegang ViewModel biar memenuhi poin ViewModel
    val uiState by productViewModel.checkoutUiState.collectAsState()

    // Daftar metode pembayaran ala Shopee
    val paymentMethods = remember {
        listOf(
            PaymentMethod(
                id = "bank_transfer",
                name = "Transfer Bank",
                description = "BCA / BRI / Mandiri / BNI"
            ),
            PaymentMethod(
                id = "ewallet",
                name = "E-Wallet",
                description = "OVO • DANA • GoPay • ShopeePay"
            ),
            PaymentMethod(
                id = "cod",
                name = "COD (Bayar di Tempat)",
                description = "Bayar saat barang diterima"
            )
        )
    }

    // Launcher buat pilih gambar dari galeri (bukti pembayaran)
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Kirim ke ViewModel untuk di-upload ke Cloud Storage
                productViewModel.uploadPaymentProof(it)
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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

            // ====== Konten utama (scroll) ======
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // SECTION: Alamat (dummy, tinggal kamu sambung ke DB kalau mau)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Alamat Pengiriman", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Nama Penerima: Naylah Yasmin\nAlamat: Jl.Veteran No.1, Lowokwaru\nKota: Malang, Provinsi: Jawa Timur, Kode Pos: 12345",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // SECTION: Metode Pembayaran
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Metode Pembayaran", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))

                        paymentMethods.forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        productViewModel.selectPaymentMethod(method.id)
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Indicator ala radio
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (uiState.selectedMethodId == method.id)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                Color(0xFFE0E0E0)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uiState.selectedMethodId == method.id) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(method.name, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        method.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            if (method != paymentMethods.last()) {
                                Divider(color = Color(0xFFE0E0E0))
                            }
                        }
                    }
                }

                // SECTION: Upload Bukti Pembayaran
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Bukti Pembayaran", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Upload screenshot/struk transfer setelah kamu melakukan pembayaran.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { pickImageLauncher.launch("image/*") },
                                enabled = !uiState.isUploading
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(if (uiState.isUploading) "Mengunggah..." else "Pilih Gambar")
                            }

                            // Tampilkan thumbnail kalau sudah ada url / lokal uri
                            val proofUrl = uiState.paymentProofUrl
                            if (proofUrl != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = proofUrl),
                                    contentDescription = "Bukti pembayaran",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        if (uiState.paymentProofUrl != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Bukti pembayaran sudah tersimpan.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // SECTION: Ringkasan Pembayaran
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Ringkasan Pembayaran", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Belanja")
                            Text("$${String.format("%.2f", totalPrice)}") // contoh konversi
                        }

                        Divider(Modifier.padding(vertical = 8.dp))
                    }
                }

                uiState.errorMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // ====== Bagian bawah: total + tombol bayar ======
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Pembayaran", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            "$${String.format("%.2f", totalPrice)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            productViewModel.placeOrder(
                                onSuccess = {
                                    // Setelah sukses simpan ke Supabase, bisa balik ke home / tampilkan screen sukses
                                    navController.navigate("order_success") {
                                        popUpTo("cart") { inclusive = true }
                                    }
                                }
                            )
                        },
                        enabled = !uiState.isPlacingOrder &&
                                uiState.selectedMethodId != null &&
                                uiState.paymentProofUrl != null,
                        modifier = Modifier
                            .height(48.dp)
                    ) {
                        Text(
                            if (uiState.isPlacingOrder) "Memproses..." else "Buat Pesanan",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}