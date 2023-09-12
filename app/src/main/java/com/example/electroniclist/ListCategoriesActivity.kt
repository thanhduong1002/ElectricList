package com.example.electroniclist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electroniclist.adapter.CategoryListAdapter
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.retrofit.ServiceInterface
import com.example.electroniclist.viewmodel.CategoryViewModel
import com.example.electroniclist.viewmodel.ProductViewModel

class ListCategoriesActivity : AppCompatActivity() {
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var repository: ProductRepository
    private lateinit var productDao: ProductDao
    private lateinit var serviceInterface: ServiceInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_categories)

        val appDatabase = AppDatabase.getDatabase(this)
        productDao = appDatabase.productDao()

        repository = ProductRepository(productDao, appDatabase)
        productViewModel = ProductViewModel(repository)
//        productViewModel = ViewModelProvider(this, ProductViewModelFactory(repository)).get(ProductViewModel::class.java)
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
//        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        val recyclerViewHorizontal: RecyclerView = findViewById(R.id.recyclerViewCategories)

        val adapter = CategoryListAdapter(emptyList(), productViewModel)
        recyclerViewHorizontal.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewHorizontal.adapter = adapter

        categoryViewModel.categoryList.observe(this, Observer { categories ->
            adapter.categoryList = categories
            adapter.notifyDataSetChanged()
        })

        categoryViewModel.fetchCategories()
    }
}