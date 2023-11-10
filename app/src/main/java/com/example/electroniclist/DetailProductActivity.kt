package com.example.electroniclist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.electroniclist.data.Products
import com.example.electroniclist.databinding.ActivityDetailProductBinding
import com.example.electroniclist.retrofit.ServiceBuilder
import com.example.electroniclist.retrofit.ServiceInterface
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailProductBinding
    private var titleScreen: String = "Detail"
    private lateinit var data: Products

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent?.extras?.getString(ProductId)
        val content = intent?.extras?.getString(TitleProduct)

        if (!content.isNullOrEmpty()) {
            titleScreen = content.toString()
        }
        supportActionBar!!.title = titleScreen

        if (id != null) {
            binding.productId.text = id.toString()
            initViewModel(id)
        } else binding.productId.text = "Nothing"
    }

    companion object {
        const val ProductId = "productId"
        const val TitleProduct = "productTitle"
    }

    @SuppressLint("SetTextI18n")
    private fun initViewModel(id: String) {
        val retrofit = ServiceBuilder.buildService(ServiceInterface::class.java)

        retrofit.getDetailProduct(id.toInt()).enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                try {
                    data = response.body()!!

                    Picasso.get().load(data.images?.get(0)).into(binding.imageProduct)
                    binding.productTitle.text = "Name: ${data.title}"
                    binding.productDescription.text = "Description: ${data.description}"
                    binding.productPrice.text = "Price: $${data.price}"
                    binding.productRating.text = "Rating: ${data.rating} star"

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
                Log.e("Failed", "Api Failed" + t.message)
            }

        })
    }
}