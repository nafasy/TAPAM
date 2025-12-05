package com.example.tugas1.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.tugas1.model.Product

class ProductViewModel : ViewModel() {

    // Contoh produk awal
    private val _products = mutableStateListOf(
        Product("1", "Tas Cantik", 100, "Tas elegan", null),
        Product("2", "Sepatu Sport", 150, "Sepatu nyaman", null)
    )
    val products: List<Product> get() = _products

    fun getProductById(id: String): Product? {
        return _products.find { it.id == id }
    }

    fun updateProduct(id: String, name: String, price: Int, description: String, imageUri: Uri?) {
        val product = getProductById(id)
        product?.let {
            it.name = name
            it.price = price
            it.description = description
            if (imageUri != null) {
                it.imageUrl = imageUri.toString() // Untuk sementara simpan Uri.toString
            }
        }
    }
}
