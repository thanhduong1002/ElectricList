package com.example.electroniclist.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.electroniclist.data.Products
import com.example.electroniclist.data.asEntity
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.local.entities.ProductEntity
import com.example.electroniclist.retrofit.ServiceInterface
import com.example.electroniclist.viewmodel.ProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductRepository constructor(
    private val productDao: ProductDao,
    private val database: AppDatabase
) {
    fun getAllProducts(): List<ProductEntity> = productDao.getProducts()

    fun getProductsByCategory(category: String): List<ProductEntity> =
        productDao.getProductsByCategory(category)

    fun getDetailProduct(productId: Int): Flow<ProductEntity> =
        productDao.getDetailProduct(productId)

    fun insertAll(products: List<ProductEntity>) {
        CoroutineScope(Dispatchers.IO).launch {
            database.productDao().insertAll(products)
        }
    }

    fun insertProduct(productEntity: ProductEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            database.productDao().insertProduct(productEntity)
        }
    }
}

