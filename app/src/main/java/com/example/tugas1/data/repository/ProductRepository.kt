//package com.example.tugas1.data.repository
//
//import com.example.tugas1.model.Product // 👈 PERBAIKAN IMPORT
//import io.github.jan.supabase.SupabaseClient
//import io.github.jan.supabase.postgrest.postgrest
//import io.github.jan.supabase.storage.storage
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import java.io.File
//
//class ProductRepository(private val client: SupabaseClient) {
//
//    private val productsTable = client.postgrest["products"]
//    private val imagesBucket = client.storage["product_images"]
//
//    suspend fun getProducts(): List<Product> {
//        return withContext(Dispatchers.IO) {
//            productsTable.select().decodeList<Product>()
//        }
//    }
//
//    // Menggunakan 'id' dalam path storage
//    suspend fun uploadProductImage(file: File, id: String): String {
//        return withContext(Dispatchers.IO) {
//            val path = "images/$id/${file.name}" // 👈 MENGGUNAKAN ID
//            imagesBucket.upload(path, file.readBytes(), upsert = true)
//            imagesBucket.publicUrl(path)
//        }
//    }
//
//    suspend fun insertProduct(product: Product) {
//        withContext(Dispatchers.IO) {
//            productsTable.insert(product)
//        }
//    }
//
//    suspend fun updateProduct(product: Product) {
//        withContext(Dispatchers.IO) {
//            productsTable.update(product) {
//                filter { eq("id", product.id) } // 👈 MENGGUNAKAN ID
//            }
//        }
//    }
//}