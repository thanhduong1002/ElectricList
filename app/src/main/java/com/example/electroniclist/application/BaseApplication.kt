package com.example.electroniclist.application

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.entities.ProductEntity
import com.example.electroniclist.viewmodel.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BaseApplication : Application() {
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
//    init {
//        GlobalScope.launch(Dispatchers.IO) {
//            val productViewModel = database.productViewModel()
//            val products = productViewModel.productsList
//            Log.d("products", "$products")
////            productDao.insertAll(products)
//        }
//    }
