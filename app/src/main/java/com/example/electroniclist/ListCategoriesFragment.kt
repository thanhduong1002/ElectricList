package com.example.electroniclist

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electroniclist.adapter.CategoryListAdapter
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.retrofit.ServiceInterface
import com.example.electroniclist.viewmodel.CategoryViewModel
import com.example.electroniclist.viewmodel.ProductViewModel


class ListCategoriesFragment : Fragment() {
    private lateinit var categoryAdapter: CategoryListAdapter
    private lateinit var productViewModel: ProductViewModel
    private lateinit var repository: ProductRepository
    private lateinit var productDao: ProductDao
    private lateinit var serviceInterface: ServiceInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)

//        repository = ProductRepository(productDao, serviceInterface)
//        productViewModel = ViewModelProvider(this, ProductViewModelFactory(repository)).get(ProductViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewCategories)
        categoryAdapter = CategoryListAdapter(emptyList(), productViewModel)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = categoryAdapter

        val categoryViewModel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        categoryViewModel.categoryList.observe(viewLifecycleOwner, Observer { categories ->
            categoryAdapter.categoryList = categories
            categoryAdapter.notifyDataSetChanged()
        })

        categoryViewModel.fetchCategories()
    }

}