package com.example.electroniclist

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.electroniclist.databinding.FragmentDetailProductBinding
import com.example.electroniclist.viewmodel.ProductViewModel
import com.example.electroniclist.viewmodel.SharedViewModel
import com.squareup.picasso.Picasso

class DetailProductFragment : Fragment() {
    private lateinit var binding: FragmentDetailProductBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private var productId: Int = 0
    private var productName: String = ""
    private var productDescription: String = ""
    private var productPrice: String = ""
    private var productRating: String = ""
    private var productImage: ArrayList<String> ?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        productViewModel = sharedViewModel.productViewModel.value ?: throw IllegalArgumentException(
            "ProductViewModel not set."
        )

        val arguments: Bundle? = arguments
        if (arguments != null) {
            productId = arguments.getString(ProductId)?.toInt() ?: 0
            productName = arguments.getString(TitleProduct) ?: "Name Product"
            productDescription = arguments.getString(DescriptionProduct) ?: "Description Product"
            productPrice = arguments.getString(ProductPrice) ?: "Price Product"
            productRating = arguments.getString(ProductRating) ?: "Rating Product"
            productImage = arguments.getString(ProductImage)?.split(",")?.toMutableList() as ArrayList<String>?
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.setActionBarTitle(productName)

        productViewModel.detailProduct.observe(viewLifecycleOwner) { product ->
            Picasso.get().load(product.images?.get(0)).into(binding.imageProduct)
            binding.productTitle.text = "Name: ${product.title}"
            binding.productDescription.text = "Description: ${product.description}"
            binding.productPrice.text = "Price: $${product.price}"
            binding.productRating.text = "Rating: ${product.rating} star"
        }

        checkAndGetProductByProductId(productId)
    }

    companion object {
        const val ProductId = "ProductId"
        const val TitleProduct = "TitleProduct"
        const val DescriptionProduct = "DescriptionProduct"
        const val ProductPrice = "ProductPrice"
        const val ProductRating = "ProductRating"
        const val ProductImage = "ProductImage"
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun checkAndGetProductByProductId(productId: Int) {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        val hasWifi = networkInfo?.type == ConnectivityManager.TYPE_WIFI

        if (hasWifi) {
            productViewModel.getDetailProduct(productId)
        } else {
            binding.productId.text = productId.toString()
            binding.productRating.text = "Rating: $productRating star"
            binding.productDescription.text = "Description: $productDescription"
            Picasso.get().load(productImage?.get(0)?.substring(1)).into(binding.imageProduct)
            binding.productTitle.text = "Name: $productName"
            binding.productPrice.text = "Price: $productPrice"
        }
    }
}