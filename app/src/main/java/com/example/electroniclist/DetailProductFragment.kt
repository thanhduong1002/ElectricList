package com.example.electroniclist

import android.annotation.SuppressLint
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

        productViewModel.getDetailProduct(productId)
    }

    companion object {
        const val ProductId = "ProductId"
        const val TitleProduct = "TitleProduct"
    }
}