package com.example.tugas1.ui.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tugas1.viewmodel.ProductViewModel

@Composable
fun ProductDetailScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    productId: String
) {
    val product = productViewModel.getProductById(productId)
    var productName by remember { mutableStateOf(TextFieldValue(product?.name ?: "")) }
    var productPrice by remember { mutableStateOf(TextFieldValue(product?.price.toString())) }
    var productDesc by remember { mutableStateOf(TextFieldValue(product?.description ?: "")) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Product Detail", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(model = imageUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else if (!product?.imageUrl.isNullOrEmpty()) {
                AsyncImage(model = product?.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Text("No Image", color = Color.White)
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = productPrice, onValueChange = { productPrice = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = productDesc, onValueChange = { productDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp))
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                productViewModel.updateProduct(
                    productId,
                    productName.text,
                    productPrice.text.toIntOrNull() ?: 0,
                    productDesc.text,
                    imageUri
                )
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Save")
        }
    }
}
