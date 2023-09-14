package com.example.electroniclist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.viewmodel.ProductViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var repository: ProductRepository
    private lateinit var productDao: ProductDao
    private var appPausedTime: Long = 0
    private var appInBackground = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer1, ListCategoriesFragment())
            replace(R.id.fragmentContainer2, ListProductsFragment())
            commit()
        }

        val floatBtn: FloatingActionButton = findViewById(R.id.floatingActionButton)
        floatBtn.setOnClickListener{
            val intent = Intent(this, AddProductActivity::class.java)
            intent.putExtra(AddProductActivity.Title, "Add Product")
            this.startActivity(intent)
        }
        val appDatabase = AppDatabase.getDatabase(this)
        productDao = appDatabase.productDao()
        repository = ProductRepository(productDao, appDatabase)
        productViewModel = ProductViewModel(repository)

    }

    override fun onPause() {
        super.onPause()
        appPausedTime = System.currentTimeMillis()
        appInBackground = true
    }

    override fun onResume() {
        super.onResume()
        if (appInBackground) {
            val elapsedTime = System.currentTimeMillis() - appPausedTime
            if (elapsedTime > (2 * 60 * 1000)) {
                Log.d("reopen", "da qua 2 phut")
                productViewModel.setReopenEvent(true)
            }
        }
        appInBackground = false
    }
}