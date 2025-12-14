package com.example.tugas1.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.tugas1.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductViewModel : ViewModel() {

    private val _productList = MutableStateFlow(
        listOf(
            // PERBAIKAN INISIALISASI
            Product(
                id = "1",
                name = "Laptop Pro",
                price = 12000000.0, // <-- DOUBLE
                description = "Laptop kencang untuk kerja",
                imageUrl = "",
                category = "Elektronik", // <-- NILAI WAJIB
                createdAt = "2025-12-14" // <-- NILAI WAJIB
            ),
            Product(
                id = "2",
                name = "Keyboard Gaming",
                price = 350000.0, // <-- DOUBLE
                description = "RGB full warna",
                imageUrl = "",
                category = "Aksesoris", // <-- NILAI WAJIB
                createdAt = "2025-12-14" // <-- NILAI WAJIB
            ),
            Product(
                id = "3",
                name = "Mouse Wireless",
                price = 150000.0, // <-- DOUBLE
                description = "Nyaman dan ringan",
                imageUrl = "",
                category = "Aksesoris", // <-- NILAI WAJIB
                createdAt = "2025-12-14" // <-- NILAI WAJIB
            )
        )
    )
    val productList: StateFlow<List<Product>> = _productList

    fun getProductById(id: String): Product? {
        return _productList.value.firstOrNull { it.id == id }
    }

    fun updateProduct(
        id: String,
        name: String,
        price: Int,
        description: String,
        imageUri: Uri?
    ) {
        val current = _productList.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }

        if (index != -1) {
            val old = current[index]

            current[index] = old.copy(
                name = name,
                price = price.toDouble(), // <-- PERBAIKAN: Konversi ke Double
                description = description,
                imageUrl = imageUri?.toString() ?: old.imageUrl,
                // PERBAIKAN: Salin parameter wajib yang tidak diubah
                category = old.category,
                createdAt = old.createdAt
            )

            _productList.value = current
        }
    }

    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    val cartItems: StateFlow<List<Product>> = _cartItems

    fun addToCart(product: Product) {
        _cartItems.value = _cartItems.value + product
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}