package com.example.electroniclist.data.repository

import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.CategoryDao
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.local.entities.CategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryRepository constructor(
    private val categoryDao: CategoryDao,
    private val database: AppDatabase
) {
    fun getCategories(): List<CategoryEntity> = categoryDao.getCategories()

    fun insertAll(categories: List<CategoryEntity>) {
        CoroutineScope(Dispatchers.IO).launch {
            database.categoryDao().insertAll(categories)
        }
    }
}