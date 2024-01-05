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
    private val _productsList: MutableLiveData<List<Products>> = MutableLiveData(emptyList())
    val productsList: LiveData<List<Products>> = _productsList

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _productAdded: MutableLiveData<Boolean> = MutableLiveData()
    val productAdded: LiveData<Boolean> = _productAdded

    private val _productDeleted: MutableLiveData<Boolean> = MutableLiveData()
    val productDeleted: LiveData<Boolean> = _productDeleted

    var detailProduct: MutableLiveData<Products> = MutableLiveData()

    private val pageSize = 10

    fun setFalseProductAdded() {
        _productAdded.value = false
    }

    fun setFalseProductDeleted() {
        _productDeleted.value = false
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category

        if (category != "all") {
            getProductsByCategory(category)
        }
        else getAllProducts()
    }

    private val retrofit = ServiceBuilder.buildService(ServiceInterface::class.java)

    private fun getAllProducts() {
        retrofit.getAllProducts().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                try {
                    val responseBody = response.body()!!
                    val products = responseBody.products
                    val productsList: List<ProductEntity> = products.asEntity()

                    repository.insertAll(productsList)
                    Log.d("updateLiveData", "Before Update livedata list products ${productsList.size}")
                    _productsList.postValue(products)
                    Log.d("updateLiveData", "Update livedata list products ${productsList.size}")
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

    fun getDetailProduct(id: Int) {
        retrofit.getDetailProduct(id).enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                try {
                    detailProduct.value = response.body()!!
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
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

    private val _reopenEvent = MutableLiveData<Boolean>()
    val reopenEvent: LiveData<Boolean>
        get() = _reopenEvent

    fun setReopenEvent(value: Boolean) {
        _reopenEvent.value = value
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun loadMoreProducts(currentPage: Int) {
        _isLoading.postValue(true)

        Log.d("page", "$currentPage")

        val offset = currentPage * pageSize

        retrofit.getProductsByPaging(pageSize, offset).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                try {
                    val responseBody = response.body()!!
                    val products = responseBody.products
                    val currentList = _productsList.value.orEmpty()
                    var updatedList = currentList.toMutableList()

                    updatedList.addAll(products)
                    //update list thì gặp tình trạng bị lặp id làm list đủ số lượng nhưng thiếu vài đoạn id từ 50 - 60, 90 - 100 -> cần lọc theo id rồi mới trả về cho productList
                    updatedList = updatedList.distinctBy {it.id}.toMutableList()

                    _productsList.postValue(updatedList)
                    Log.d("page", "${_productsList.value?.size}")
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _isLoading.postValue(false)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("Failed", "Api Failed" + t.message)

                _isLoading.postValue(false)
            }
        })
    }

    fun getFirstPageProducts() {
        val offset = 0 * pageSize

        retrofit.getProductsByPaging(pageSize, offset).enqueue(object : Callback<ApiResponse> {
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

    fun getProductsByCategoryOffline(category: String) = repository.getProductsByCategory(category)
}