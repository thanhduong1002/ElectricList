package com.example.electroniclist.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.electroniclist.retrofit.ServiceBuilder
import com.example.electroniclist.retrofit.ServiceInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel : ViewModel() {
    val categoryList: MutableLiveData<List<String>> = MutableLiveData()

    fun fetchCategories() {
        val retrofit = ServiceBuilder.buildService(ServiceInterface::class.java)
        retrofit.getAllCategories().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    categoryList.postValue(categories)
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("Failed", "Api Failed" + t.message)
            }
        })
    }
}