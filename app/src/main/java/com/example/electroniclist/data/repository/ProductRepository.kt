package com.example.electroniclist.data.repository

import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.local.entities.ProductEntity
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

    fun deleteAll() {
        database.productDao().deleteAll()
    }
}

