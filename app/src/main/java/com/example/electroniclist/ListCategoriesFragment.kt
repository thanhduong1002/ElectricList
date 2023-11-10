package com.example.electroniclist

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electroniclist.adapter.CategoryListAdapter
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.CategoryDao
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.local.entities.asCategoryList
import com.example.electroniclist.data.repository.CategoryRepository
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.databinding.FragmentListCategoriesBinding
import com.example.electroniclist.viewmodel.CategoryViewModel
import com.example.electroniclist.viewmodel.ProductViewModel
import com.example.electroniclist.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListCategoriesFragment : Fragment() {
    private lateinit var categoryAdapter: CategoryListAdapter
    private lateinit var productViewModel: ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var productDao: ProductDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var productRepository: ProductRepository
    private lateinit var categoryRepository: CategoryRepository
    private var isDataLoaded = false
    private lateinit var binding: FragmentListCategoriesBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appDatabase = AppDatabase.getDatabase(requireContext())

        productDao = appDatabase.productDao()
        categoryDao = appDatabase.categoryDao()
        productRepository = ProductRepository(productDao, appDatabase)
//        productViewModel = ProductViewModel(productRepository)
        categoryRepository = CategoryRepository(categoryDao, appDatabase)
        categoryViewModel = CategoryViewModel(categoryRepository)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        productViewModel = sharedViewModel.productViewModel.value ?: throw IllegalArgumentException("ProductViewModel not set.")
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryAdapter = CategoryListAdapter(emptyList(), productViewModel)
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategories.adapter = categoryAdapter

        categoryViewModel.categoryList.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.categoryList = categories
            categoryAdapter.notifyDataSetChanged()
        }

        productViewModel.reopenEvent.observe(viewLifecycleOwner) { reopen ->
            Log.d("reopen", "reload data")

            if (reopen) {
                refreshData()
            }
        }
        refreshData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshData() {
        if (!isDataLoaded) {
            Log.d("reopenCate", "Load lai")

            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val networkInfo = connectivityManager?.activeNetworkInfo
            val hasWifi = networkInfo?.type == ConnectivityManager.TYPE_WIFI

            if (hasWifi) {
                categoryViewModel.fetchCategories()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val listCategoryEntity = categoryViewModel.getAllCategoriesFromDB()

                    withContext(Dispatchers.Main) {
                        categoryAdapter.categoryList = listCategoryEntity.asCategoryList()
                        categoryAdapter.notifyDataSetChanged()
                    }
                }
            }
            isDataLoaded = true
        }
    }
}