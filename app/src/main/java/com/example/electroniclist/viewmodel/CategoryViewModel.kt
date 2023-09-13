package com.example.electroniclist.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.electroniclist.data.local.entities.CategoryEntity
import com.example.electroniclist.data.local.entities.ProductEntity
import com.example.electroniclist.data.local.entities.asCategoryEntities
import com.example.electroniclist.data.repository.CategoryRepository
import com.example.electroniclist.retrofit.ServiceBuilder
import com.example.electroniclist.retrofit.ServiceInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {
    val categoryList: MutableLiveData<List<String>> = MutableLiveData()

    fun fetchCategories() {
        val retrofit = ServiceBuilder.buildService(ServiceInterface::class.java)
        retrofit.getAllCategories().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    (categories?.asCategoryEntities() ?: null)?.let { insertAllCategories(it) }
                    categoryList.postValue(categories)
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("Failed", "Api Failed" + t.message)
            }
        })
    }

    fun getAllCategoriesFromDB(): List<CategoryEntity> = repository.getCategories()

    fun insertAllCategories(categories: List<CategoryEntity>) = repository.insertAll(categories)
}