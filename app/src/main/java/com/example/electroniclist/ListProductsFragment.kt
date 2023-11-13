package com.example.electroniclist

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electroniclist.adapter.ElectricListAdapter
import com.example.electroniclist.data.local.entities.asApiResponse
import com.example.electroniclist.databinding.FragmentListProductsBinding
import com.example.electroniclist.viewmodel.ProductViewModel
import com.example.electroniclist.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListProductsFragment : Fragment(), ProductAdapterListener {
    private lateinit var productAdapter: ElectricListAdapter
    private lateinit var productViewModel: ProductViewModel
    private var isDataLoaded = false
    private lateinit var binding: FragmentListProductsBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        productViewModel= sharedViewModel.productViewModel.value ?: throw IllegalArgumentException("ProductViewModel not set.")
    }

    @DelicateCoroutinesApi
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productAdapter = ElectricListAdapter(emptyList())
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = productAdapter

        productAdapter.setListener(this)

        productViewModel.productsList.observe(viewLifecycleOwner) { products ->
            productAdapter.setProductsList(products)
            productAdapter.notifyDataSetChanged()
        }

        GlobalScope.launch(Dispatchers.Main) {
            productViewModel.selectedCategory.observe(
                viewLifecycleOwner
            ) { selectedCategory ->
                productViewModel.getProductsByCategory(selectedCategory)
            }
        }

        productViewModel.productAdded.observe(viewLifecycleOwner) { _ ->
            productViewModel.setFalseProductAdded()
        }

        productViewModel.productDeleted.observe(viewLifecycleOwner) { productDeleted ->
            if (productDeleted) {
                productViewModel.setFalseProductDeleted()
            }
        }

        productViewModel.reopenEvent.observe(viewLifecycleOwner) { reopen ->
            if (reopen) {
                refreshData()
            }
        }

        firstLoad()
    }

    override fun onDeleteProduct(productId: String) {
//        repository = ProductRepository(productDao)
//        productViewModel = ProductViewModel(repository)

//        val productViewModel = ViewModelProvider(requireActivity()).get(ProductViewModel::class.java)
//        repository = ProductRepository(productDao, serviceInterface)
//        val productViewModel = ViewModelProvider(this, ProductViewModelFactory(repository)).get(ProductViewModel::class.java)

//        productViewModel.deleteProduct(productId)
    }

    private fun firstLoad() {
        Log.d("firstLoad", "firstLoad: ")
        if (!isDataLoaded) {
            checkAndLoadData()

            isDataLoaded = true
        }
    }

    private fun refreshData() {
        Log.d("refreshData", "refreshData: ")
        checkAndLoadData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkAndLoadData() {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        val hasWifi = networkInfo?.type == ConnectivityManager.TYPE_WIFI

        if (hasWifi) {
            productViewModel.getAllProducts()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val listProductEntity = productViewModel.getAllProductsFromDB()

                withContext(Dispatchers.Main) {
                    productAdapter.setProductsList(listProductEntity.asApiResponse())
                    productAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}

