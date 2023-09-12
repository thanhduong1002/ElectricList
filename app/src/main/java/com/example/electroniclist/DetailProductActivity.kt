package com.example.electroniclist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        setContentView(R.layout.activity_detail_product)

        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent?.extras?.getString(ProductId)
        val content = intent?.extras?.getString(TitleProduct)
        if (!content.isNullOrEmpty()) {
            titleScreen = content.toString()
        }
        supportActionBar!!.title = titleScreen

        val textId: TextView = binding.productId
        if (id != null) {
            textId.text = id.toString()
            initViewModel(id)
        } else textId.text = "Nothing"
    }
    companion object {
        const val ProductId = "productId"
        const val TitleProduct = "productTitle"
    }
    private fun initViewModel(id: String){
        val retrofit = ServiceBuilder.buildService(ServiceInterface::class.java)

        retrofit.getDetailProduct(id.toInt()).enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                try {
                    data = response.body()!!

                    val imageItem : ImageView = binding.imageProduct
                    Picasso.get().load(data?.images?.get(0)).into(imageItem)

                    val titleItem : TextView = binding.productTitle
                    titleItem.text = "Name: " + data.title.toString()

                    val descripItem : TextView = binding.productDescription
                    descripItem.text = "Description: " + data.description.toString()

                    val priceItem : TextView = binding.productPrice
                    priceItem.text = "Price: $" + data.price.toString()

                    val ratingItem : TextView = binding.productRating
                    ratingItem.text = "Rating: " + data.rating.toString() + " star"

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