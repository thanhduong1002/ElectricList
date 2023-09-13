package com.example.electroniclist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.electroniclist.data.local.entities.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categories: List<CategoryEntity>)


}