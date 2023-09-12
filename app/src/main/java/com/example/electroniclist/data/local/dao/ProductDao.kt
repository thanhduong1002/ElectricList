package com.example.electroniclist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.electroniclist.data.local.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getProducts(): List<ProductEntity>

    @Query("SELECT * FROM product WHERE category = :category")
    fun getProductsByCategory(category: String): List<ProductEntity>

    @Query("SELECT * FROM product WHERE id = :id")
    fun getDetailProduct(id: Int): Flow<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<ProductEntity>)
}