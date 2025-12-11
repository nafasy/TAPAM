package com.example.tugas1.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugas1.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ===============================
// UI State untuk halaman checkout
// ===============================
data class CheckoutUiState(
    val selectedMethodId: String? = null,
    val paymentProofUrl: String? = null,
    val isUploading: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val errorMessage: String? = null
)

class ProductViewModel : ViewModel() {

    // =================================
    // === PRODUCT LIST (kode awalmu) ==
    // =================================
    private val _productList = MutableStateFlow(
        listOf(
            Product("1", "Laptop Pro", 12000000, "Laptop kencang untuk kerja", ""),
            Product("2", "Keyboard Gaming", 350000, "RGB full warna", ""),
            Product("3", "Mouse Wireless", 150000, "Nyaman dan ringan", "")
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
                price = price,
                description = description,
                imageUrl = imageUri?.toString() ?: old.imageUrl
            )

            _productList.value = current
        }
    }

    // ===============================
    // === CART (kode awalmu) ========
    // ===============================
    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    val cartItems: StateFlow<List<Product>> = _cartItems

    fun addToCart(product: Product) {
        _cartItems.value = _cartItems.value + product
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Total harga cart (buat Checkout)
    private val _cartTotalPrice = MutableStateFlow(0.0)
    val cartTotalPrice: StateFlow<Double> = _cartTotalPrice

    fun setCartTotal(total: Double) {
        _cartTotalPrice.value = total
    }

    // ===============================
    // ====== CHECKOUT FEATURE =======
    // ===============================

    private val _checkoutUiState = MutableStateFlow(CheckoutUiState())
    val checkoutUiState: StateFlow<CheckoutUiState> = _checkoutUiState

    // Pilih metode pembayaran
    fun selectPaymentMethod(id: String) {
        _checkoutUiState.value = _checkoutUiState.value.copy(
            selectedMethodId = id,
            errorMessage = null
        )
    }

    // Upload bukti pembayaran
    fun uploadPaymentProof(uri: Uri) {
        viewModelScope.launch {
            _checkoutUiState.value = _checkoutUiState.value.copy(
                isUploading = true,
                errorMessage = null
            )

            try {
                // TODO: ganti pake upload storage asli ke Supabase Storage / Firebase
                val url = fakeUploadToCloudStorage(uri)

                _checkoutUiState.value = _checkoutUiState.value.copy(
                    isUploading = false,
                    paymentProofUrl = url
                )
            } catch (e: Exception) {
                _checkoutUiState.value = _checkoutUiState.value.copy(
                    isUploading = false,
                    errorMessage = "Gagal mengunggah bukti pembayaran"
                )
            }
        }
    }

    // Simpan pesanan (insert ke Supabase)
    fun placeOrder(onSuccess: () -> Unit) {
        val state = _checkoutUiState.value

        if (state.selectedMethodId == null || state.paymentProofUrl == null) {
            _checkoutUiState.value = state.copy(
                errorMessage = "Pilih metode pembayaran & upload bukti dulu."
            )
            return
        }

        viewModelScope.launch {
            _checkoutUiState.value = state.copy(
                isPlacingOrder = true,
                errorMessage = null
            )

            try {
                // TODO: ganti pake insert Supabase beneran
                fakeInsertOrderToSupabase()

                _checkoutUiState.value = _checkoutUiState.value.copy(
                    isPlacingOrder = false
                )

                clearCart() // kosongkan keranjang
                onSuccess()
            } catch (e: Exception) {
                _checkoutUiState.value = _checkoutUiState.value.copy(
                    isPlacingOrder = false,
                    errorMessage = "Gagal membuat pesanan"
                )
            }
        }
    }

    // ===============================
    // ==== Dummy Storage & Supabase =
    // ===============================

    private suspend fun fakeUploadToCloudStorage(uri: Uri): String {
        // nanti ganti pakai Supabase Storage
        return uri.toString()
    }

    private suspend fun fakeInsertOrderToSupabase() {
        // nanti ganti pakai supabase.from("orders").insert(...)
    }
}