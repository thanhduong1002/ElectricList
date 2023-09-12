package com.example.electroniclist.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.electroniclist.data.Products
import com.example.electroniclist.data.asEntity
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.local.entities.ProductEntity
import com.example.electroniclist.retrofit.ServiceInterface
import com.example.electroniclist.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.Flow

class ProductRepository constructor(
    private val productDao: ProductDao
) {
    fun getAllProducts(): List<ProductEntity> = productDao.getProducts()

    fun getProductsByCategory(category: String): List<ProductEntity> =
        productDao.getProductsByCategory(category)

    fun getDetailProduct(productId: Int): Flow<ProductEntity> =
        productDao.getDetailProduct(productId)

    fun insertAll(products: List<ProductEntity>) = productDao.insertAll(products)
}
//    suspend fun refreshData() {
//        try {
//            val response = retrofit.getAllProducts().execute()
//            if (response.isSuccessful) {
//                val responseBody = response.body()
//                val products = responseBody?.products ?: emptyList()
//                insertAll(products.asEntity())
//            } else {
//                Log.e("Failed", "Api Failed")
//            }
//        } catch (e: Exception) {
//            Log.e("Failed", "Api Failed" + e.message)
//        }
//    }

