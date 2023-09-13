package com.example.electroniclist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.electroniclist.ProductCallback
import com.example.electroniclist.data.ApiResponse
import com.example.electroniclist.data.Products
import com.example.electroniclist.data.asEntity
import com.example.electroniclist.data.local.entities.ProductEntity
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.retrofit.ServiceBuilder
import com.example.electroniclist.retrofit.ServiceInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel(val repository: ProductRepository) : ViewModel() {
    private val _productDetail: MutableLiveData<Products> = MutableLiveData()
    val productDetail: LiveData<Products> = _productDetail

    private val _productsList: MutableLiveData<List<Products>> = MutableLiveData()
    val productsList: LiveData<List<Products>> = _productsList

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String>
        get() = _selectedCategory

    private val _productAdded: MutableLiveData<Boolean> = MutableLiveData()
    val productAdded: LiveData<Boolean> = _productAdded

    private val _productDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val productDeleted: LiveData<Boolean> = _productDeleted

    fun setFalseProductAdded() {
        _productAdded.value = false
    }

    fun setFalseProductDeleted() {
        _productDeleted.value = false
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        Log.d("itemPDVMD", "$category")
        if (category != "all") {
            getProductsByCategory(category)
        } else getAllProducts()
    }

    private val retrofit = ServiceBuilder.buildService(ServiceInterface::class.java)

    fun getAllProducts() {
        retrofit.getAllProducts().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                try {
                    val responseBody = response.body()!!
                    val products = responseBody.products
                    val productsList: List<ProductEntity> = products.asEntity()
                    repository.insertAll(productsList)
                    _productsList.postValue(products)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("Failed", "Api Failed" + t.message)
            }
        })
    }

    fun getProductsByCategory(category: String) {
        retrofit.getProductsByCategory(category).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                try {
                    val responseBody = response.body()!!
                    val products = responseBody.products
                    _productsList.postValue(products)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("Failed", "Api Failed" + t.message)
            }
        })
    }

    fun addProduct(products: Products) {
        retrofit.addProduct(products).enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d("Api successful", "$responseBody")
                        _productAdded.postValue(true)
                    }
                } else {
                    Log.d("Api Failed", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
                Log.e("Failed", "Api Failed" + t.message)
            }

        })
    }

    fun getDetailProduct(id: String, callback: ProductCallback) {
        retrofit.getDetailProduct(id.toInt()).enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                try {
                    val product: Products? = response.body()
                    callback.onProductReceived(product)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    callback.onApiFailure("Error processing response")
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
                Log.e("Failed", "Api Failed" + t.message)
                callback.onApiFailure("API call failed")
            }

        })
    }

    fun updateProduct(id: String, products: Products) {
        retrofit.updateProduct(id.toInt(), products).enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                try {
                    val product: Products? = response.body()
                    if (product != null) {
                        Log.d("Api successful", "$product")
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
                Log.e("Failed", "Api Failed" + t.message)
            }

        })
    }

    fun deleteProduct(id: String) {
        retrofit.deleteProduct(id.toInt()).enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                try {
                    val product: Products? = response.body()
                    if (product != null) {
                        Log.d("Api successful", "$product")
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
                Log.e("Failed", "Api Failed" + t.message)
            }

        })
    }

    fun getAllProductsFromDB(): List<ProductEntity> = repository.getAllProducts()
}
//class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return ProductViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}