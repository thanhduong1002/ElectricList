package com.example.electroniclist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electroniclist.adapter.CategoryListAdapter
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.CategoryDao
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.repository.CategoryRepository
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.databinding.ActivityListCategoriesBinding
import com.example.electroniclist.viewmodel.CategoryViewModel
import com.example.electroniclist.viewmodel.ProductViewModel

class ListCategoriesActivity : AppCompatActivity() {
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var repository: ProductRepository
    private lateinit var categoryDao: CategoryDao
    private lateinit var productDao: ProductDao
    private lateinit var binding: ActivityListCategoriesBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appDatabase = AppDatabase.getDatabase(this)

        productDao = appDatabase.productDao()
        categoryDao = appDatabase.categoryDao()

        repository = ProductRepository(productDao, appDatabase)
        categoryRepository = CategoryRepository(categoryDao, appDatabase)
        productViewModel = ProductViewModel(repository)
        categoryViewModel = CategoryViewModel(categoryRepository)

        val adapter = CategoryListAdapter(emptyList(), productViewModel)

        binding.recyclerViewCategories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategories.adapter = adapter

        categoryViewModel.categoryList.observe(this) { categories ->
            adapter.categoryList = categories
            adapter.notifyDataSetChanged()
        }

        categoryViewModel.fetchCategories()
    }
}