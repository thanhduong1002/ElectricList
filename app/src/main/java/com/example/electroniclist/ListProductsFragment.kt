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
import androidx.recyclerview.widget.RecyclerView
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
        productViewModel = sharedViewModel.productViewModel.value ?: throw IllegalArgumentException(
            "ProductViewModel not set."
        )
    }

    @DelicateCoroutinesApi
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productAdapter = ElectricListAdapter(emptyList())
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = productAdapter

        productAdapter.setListener(this, requireActivity())

        productViewModel.productsList.observe(viewLifecycleOwner) { products ->
            productAdapter.setProductsList(products)
            productAdapter.notifyDataSetChanged()
        }

        GlobalScope.launch(Dispatchers.Main) {
            viewLifecycleOwnerLiveData.value?.let {
                productViewModel.selectedCategory.observe(
                    it
                ) { selectedCategory ->
                    checkAndCallData(selectedCategory)
                }
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

        sharedViewModel.reOpenEvent.observe(viewLifecycleOwner) { reopen ->
            if (reopen) {
                refreshData()
            }
        }

        productViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("isLoading", "isLoading: $isLoading")
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        }

        binding.recyclerViewProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItem >= totalItemCount
                    && (productViewModel.selectedCategory.value == "all"
                            || productViewModel.selectedCategory.value == null)
                ) {
                    productViewModel.loadMoreProducts(
                        (productViewModel.productsList.value?.size?.div(
                            10
                        ) ?: 1)
                    )
                }
            }
        })

        checkAndCallData("first")
    }

    override fun onDeleteProduct(productId: String) {
        productViewModel.deleteProduct(productId)
    }

    private fun refreshData() {
        Log.d("refreshData", "refreshData: ")
        checkAndCallData("first")
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkAndCallData(category: String) {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        val hasWifi = networkInfo?.type == ConnectivityManager.TYPE_WIFI

        if (hasWifi) {
            if (category != "first") productViewModel.getProductsByCategory(category)
            else productViewModel.getFirstPageProducts()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val listProducts =
                    if (category != "all" && category != "first") productViewModel.getProductsByCategoryOffline(category)
                    else productViewModel.getAllProductsFromDB()

                withContext(Dispatchers.Main) {
                    productAdapter.setProductsList(listProducts.asApiResponse())
                    productAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}

