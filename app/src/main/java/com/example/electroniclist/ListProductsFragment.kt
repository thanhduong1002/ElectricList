package com.example.electroniclist

import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electroniclist.adapter.ElectricListAdapter
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.local.entities.ProductEntity
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.retrofit.ServiceInterface
import com.example.electroniclist.viewmodel.CategoryViewModel
import com.example.electroniclist.viewmodel.ProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListProductsFragment : Fragment(), ProductAdapterListener {
    private lateinit var productAdapter: ElectricListAdapter
    private lateinit var repository: ProductRepository
    private lateinit var productDao: ProductDao
    private lateinit var productViewModel: ProductViewModel
    private lateinit var serviceInterface: ServiceInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        val hasWifi = networkInfo?.type == ConnectivityManager.TYPE_WIFI

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewProducts)
        productAdapter = ElectricListAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = productAdapter

        productAdapter.setListener(this)

//        repository = ProductRepository(productDao)
//        val productViewModel = ProductViewModel(repository)
//        val productViewModel = ViewModelProvider(this, viewModelFactory).get(ProductViewModel::class.java)
        val productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)
//        repository = ProductRepository(productDao, serviceInterface)
//        val productViewModel = ViewModelProvider(this, ProductViewModelFactory(repository)).get(ProductViewModel::class.java)

        productViewModel.productsList.observe(viewLifecycleOwner, Observer { products ->
            Log.d("productsList", "aloalo")
            productAdapter.setProductsList(products)
            productAdapter.notifyDataSetChanged()
        })

        productViewModel.selectedCategory.observe(viewLifecycleOwner, Observer { selectedCategory ->
            Log.d("SelectedCategory", "Selected Category changed: $selectedCategory")
            productViewModel.getProductsByCategory(selectedCategory)
        })

        productViewModel.productAdded.observe(viewLifecycleOwner, Observer { added ->
//            if (productAdded) {
//                val dialog = AlertDialog.Builder(requireContext())
//                val textView = TextView(requireContext())
//
//                with(textView) {00000000000000000000000
//                    textView.text = "Our alert"
//                    textView.textSize = 20.0F
//                    textView.setTypeface(null, Typeface.BOLD)
//                    textView.gravity = Gravity.CENTER
//                }
//                dialog.setCustomTitle(textView)
//
//                val successMessage = "Add new product successfully"
//                dialog.setMessage(successMessage)
//                dialog.setNeutralButton("Dismiss") { dialog, which ->
//                    dialog.dismiss()
//                }
//
//                dialog.show()
                Log.d("Test", "$added")
                productViewModel.setFalseProductAdded()
//            }
        })

        productViewModel.productDeleted.observe(viewLifecycleOwner, Observer { productDeleted ->
            if (productDeleted) {
                productViewModel.setFalseProductDeleted()
            }
        })
        productViewModel.getAllProducts()

//        if (hasWifi) {
//            productViewModel.getAllProducts()
//        }
    }

    override fun onDeleteProduct(productId: String) {
//        repository = ProductRepository(productDao)
//        productViewModel = ProductViewModel(repository)
        val productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)
//        repository = ProductRepository(productDao, serviceInterface)
//        val productViewModel = ViewModelProvider(this, ProductViewModelFactory(repository)).get(ProductViewModel::class.java)
        productViewModel.deleteProduct(productId)
    }
}