package com.example.electroniclist.retrofit

import com.example.electroniclist.data.ApiResponse
import com.example.electroniclist.data.Products
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceInterface {
    @Headers("Content-Type:application/json")
    @GET("/products")
    fun getAllProducts(): Call<ApiResponse>

    @GET("/products/{id}")
    fun getDetailProduct(@Path("id") id: Int): Call<Products>

    @GET("/products/categories")
    fun getAllCategories(): Call<List<String>>

    @GET("/products/category/{category}")
    fun getProductsByCategory(@Path("category") category: String): Call<ApiResponse>

    @POST("/products/add")
    fun addProduct(@Body products: Products): Call<Products>

    @PUT("/products/{id}")
    fun updateProduct(@Path("id") id: Int, @Body products: Products): Call<Products>

    @DELETE("/products/{id}")
    fun deleteProduct(@Path("id") id: Int): Call<Products>

    @GET("/products")
    fun getProductsByPaging(@Query("limit") limit: Int, @Query("skip") skip: Int?): Call<ApiResponse>
}