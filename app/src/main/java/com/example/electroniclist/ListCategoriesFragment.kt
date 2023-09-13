package com.example.electroniclist

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electroniclist.adapter.CategoryListAdapter
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.CategoryDao
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.local.entities.asCategoryList
import com.example.electroniclist.data.repository.CategoryRepository
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.retrofit.ServiceInterface
import com.example.electroniclist.viewmodel.CategoryViewModel
import com.example.electroniclist.viewmodel.ProductViewModel
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
    private lateinit var repository: ProductRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var serviceInterface: ServiceInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_categories, container, false)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appDatabase = AppDatabase.getDatabase(requireContext())
        productDao = appDatabase.productDao()
        categoryDao = appDatabase.categoryDao()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appDatabase = AppDatabase.getDatabase(requireContext())

        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        val hasWifi = networkInfo?.type == ConnectivityManager.TYPE_WIFI

//        productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)
        repository = ProductRepository(productDao, appDatabase)
        productViewModel = ProductViewModel(repository)
//        repository = ProductRepository(productDao, serviceInterface)
//        productViewModel = ViewModelProvider(this, ProductViewModelFactory(repository)).get(ProductViewModel::class.java)

        categoryRepository = CategoryRepository(categoryDao, appDatabase)
        categoryViewModel = CategoryViewModel(categoryRepository)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewCategories)
        categoryAdapter = CategoryListAdapter(emptyList(), productViewModel)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = categoryAdapter

//        val categoryViewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        categoryViewModel.categoryList.observe(viewLifecycleOwner, Observer { categories ->
            categoryAdapter.categoryList = categories
            categoryAdapter.notifyDataSetChanged()
        })

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
    }

}