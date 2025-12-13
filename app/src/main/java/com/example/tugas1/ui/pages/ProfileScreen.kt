package com.example.tugas1.ui.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.tugas1.viewmodel.AuthViewModel
import com.example.tugas1.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel
) {
    val profile by profileViewModel.profile.collectAsState()
    val loading by profileViewModel.loading.collectAsState()
    val errorMessage by profileViewModel.errorMessage.collectAsState()
    val isAuthenticated by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    // STATE UNTUK MENGONTROL MODE EDIT
    var isEditing by remember { mutableStateOf(false) }

    // STATE UNTUK INPUT (digunakan HANYA saat isEditing = true)
    var inputFullName by remember { mutableStateOf(profile?.fullName ?: "") }
    var inputUsername by remember { mutableStateOf(profile?.username ?: "") }

    // Sinkronisasi state input dengan data profile dari ViewModel
    LaunchedEffect(profile) {
        profile?.let {
            inputFullName = it.fullName ?: ""
            inputUsername = it.username ?: ""
        }
    }

    // Tampilkan error jika ada
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            // Anda bisa tambahkan fungsi untuk mereset errorMessage di ViewModel di sini
        }
    }

    // Navigasi jika tidak terautentikasi
    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate("auth_graph") {
                popUpTo("main_graph") { inclusive = true }
            }
        }
    }

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
                        // TOMBOL SIMPAN
                        IconButton(
                            onClick = {
                                profileViewModel.updateProfile(inputFullName, inputUsername)
                                isEditing = false // Keluar dari mode edit setelah simpan
                            },
                            enabled = !loading
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = "Simpan")
                        }
                    } else {
                        // TOMBOL EDIT
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

                    // ICON EDIT FOTO (Hanya muncul jika sedang di mode edit)
                    if (isEditing) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Ganti Foto",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(6.dp)
                                .clickable {
                                    imagePickerLauncher.launch("image/*")
                                },
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // AREA INPUT/DISPLAY NAMA DAN USERNAME
                if (isEditing) {
                    // MODE EDIT (TextFields)
                    OutlinedTextField(
                        value = inputFullName,
                        onValueChange = { inputFullName = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputUsername,
                        onValueChange = { inputUsername = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // MODE VIEW (Text Biasa)
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

                // ITEM MENU (Hanya tampil di View Mode)
                if (!isEditing) {
                    ProfileMenuItem(
                        icon = Icons.Default.Notifications,
                        title = "Notification",
                        onClick = { /* Navigasi ke notification */ }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.PinDrop,
                        title = "Shipping Address",
                        onClick = { /* Navigasi ke address */ }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Key,
                        title = "Change Password",
                        onClick = { /* Navigasi ke password */ }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Tombol Sign Out (Hanya tampil di View Mode)
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
                }

                if (isEditing) {
                    // Spacer di mode edit untuk menjaga tata letak
                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol Cancel di mode edit
                    Button(
                        onClick = {
                            isEditing = false // Batalkan edit
                            // Reset input ke nilai profil saat ini
                            inputFullName = profile?.fullName ?: ""
                            inputUsername = profile?.username ?: ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Batal")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Fungsi ProfileMenuItem (tetap sama)
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