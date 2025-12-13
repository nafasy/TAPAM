package com.example.tugas1.ui.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // <-- Import LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells // <-- Import GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid // <-- Import LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.tugas1.viewmodel.AuthViewModel
import com.example.tugas1.viewmodel.ProfileViewModel

// --- DATA DUMMY UNTUK STATISTIK PROFIL (Untuk Grid) ---
data class ProfileStat(val id: Int, val count: String, val label: String)

val dummyStats = listOf(
    ProfileStat(1, "12", "Pesanan Selesai"),
    ProfileStat(2, "4.8", "Rating Rata-rata"),
    ProfileStat(3, "30", "Review Diberikan"),
)
// ------------------------------------------


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel
) {
    // ... (Semua State dan LaunchedEffect tetap sama) ...
    val profile by profileViewModel.profile.collectAsState()
    val loading by profileViewModel.loading.collectAsState()
    val errorMessage by profileViewModel.errorMessage.collectAsState()
    val isAuthenticated by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var isEditing by remember { mutableStateOf(false) }
    var inputFullName by remember { mutableStateOf(profile?.fullName ?: "") }
    var inputUsername by remember { mutableStateOf(profile?.username ?: "") }

    LaunchedEffect(profile) { profile?.let { inputFullName = it.fullName ?: ""; inputUsername = it.username ?: "" } }
    LaunchedEffect(errorMessage) { errorMessage?.let { snackbarHostState.showSnackbar(it) } }
    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate("auth_graph") { popUpTo("main_graph") { inclusive = true } }
        }
    }

    // ... (imagePickerLauncher, avatarDisplayUrl tetap sama) ...
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val imageBytes = inputStream.readBytes()
                    profileViewModel.uploadAvatar(imageBytes)
                }
            }
        }
    )

    val avatarPath = profile?.avatarUrl
    val avatarDisplayUrl = remember(avatarPath) {
        if (!avatarPath.isNullOrEmpty()) {
            profileViewModel.getAvatarPublicUrl(avatarPath)
        } else {
            "https://i.pravatar.cc/150"
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { profileViewModel.updateProfile(inputFullName, inputUsername); isEditing = false }, enabled = !loading) { Icon(Icons.Filled.Save, contentDescription = "Simpan") }
                    } else {
                        IconButton(onClick = { isEditing = true }) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                    }
                }
            )
        }
    ) { paddingValues ->

        // 🛑 MENGGANTI COLUMN DENGAN LAZYCOLUMN (Memenuhi kriteria Lazy List)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {

            item {
                if (loading && profile == null) {
                    CircularProgressIndicator()
                } else {
                    Spacer(modifier = Modifier.height(16.dp))

                    // AREA FOTO PROFIL
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                            painter = rememberAsyncImagePainter(model = avatarDisplayUrl),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )

                        if (isEditing) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Ganti Foto",
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(6.dp)
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // AREA INPUT/DISPLAY NAMA DAN USERNAME
                    if (isEditing) {
                        OutlinedTextField(value = inputFullName, onValueChange = { inputFullName = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = inputUsername, onValueChange = { inputUsername = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                    } else {
                        Text(
                            text = profile?.fullName ?: "Nama Belum Diatur",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "@${profile?.username ?: "username"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            } // End item: Header Profil

            // -------------------- LAZY VERTICAL GRID UNTUK STATISTIK --------------------
            if (!isEditing) {
                item {
                    // Menerapkan LazyVerticalGrid (Memenuhi kriteria Grid)
                    Box(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) { // IntrinsicSize.Min untuk tinggi minimum
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxWidth().height(100.dp), // Batas tinggi agar LazyColumn bisa scroll
                            verticalArrangement = Arrangement.Center,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            userScrollEnabled = false
                        ) {
                            items(dummyStats) { stat ->
                                StatGridItem(stat = stat)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // -------------------- ITEM MENU --------------------
                item {
                    ProfileMenuItem(icon = Icons.Default.Notifications, title = "Notification", onClick = { /* Navigasi */ })
                    ProfileMenuItem(icon = Icons.Default.PinDrop, title = "Shipping Address", onClick = { /* Navigasi */ })
                    ProfileMenuItem(icon = Icons.Default.Key, title = "Change Password", onClick = { /* Navigasi */ })
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    // Tombol Sign Out
                    Button(
                        onClick = { authViewModel.logout() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out", tint = Color.Red)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign Out", fontSize = 16.sp, color = Color.Red)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } // End if (!isEditing)

            // -------------------- MODE EDIT BUTTONS --------------------
            if (isEditing) {
                item {
                    // Tombol Cancel
                    Button(
                        onClick = {
                            isEditing = false
                            inputFullName = profile?.fullName ?: ""
                            inputUsername = profile?.username ?: ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } // End if (isEditing)
        } // End LazyColumn
    } // End Scaffold
}

// ... (Fungsi ProfileMenuItem tetap sama) ...
@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF4A55A2))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray)
    }
}

// 🛑 Komponen baru untuk item di dalam Grid Statistik
@Composable
fun StatGridItem(stat: ProfileStat) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        Text(
            text = stat.count,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stat.label,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}