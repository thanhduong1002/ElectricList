package com.example.electroniclist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.electroniclist.databinding.ActivityDetailProductBinding
import com.example.electroniclist.viewmodel.ProductViewModel
import com.squareup.picasso.Picasso

class DetailProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailProductBinding
    private var titleScreen: String = "Detail"
    private lateinit var productViewModel: ProductViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        val id = intent?.extras?.getString(ProductId)
        val content = intent?.extras?.getString(TitleProduct)

        if (!content.isNullOrEmpty()) {
            titleScreen = content.toString()
        }
        supportActionBar!!.apply {
            title = titleScreen
            setDisplayHomeAsUpEnabled(true)
        }

        if (id != null) {
            binding.productId.text = id.toString()
            productViewModel.getDetailProduct(id.toInt())
        } else binding.productId.text = "Nothing"

        observeDetailProduct()
    }

    companion object {
        const val ProductId = "productId"
        const val TitleProduct = "productTitle"
    }

    @SuppressLint("SetTextI18n")
    private fun observeDetailProduct() {
        productViewModel.detailProduct.observe(this) { detail ->
            binding.apply {
                Picasso.get().load(detail.images?.get(0)).into(imageProduct)
                productTitle.text = "Name: ${detail.title}"
                productDescription.text = "Description: ${detail.description}"
                productPrice.text = "Price: $${detail.price}"
                productRating.text = "Rating: ${detail.rating} star"
            }
        }
    }
}