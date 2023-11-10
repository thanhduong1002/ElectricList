package com.example.electroniclist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.databinding.ActivityHomeBinding
import com.example.electroniclist.viewmodel.ProductViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var repository: ProductRepository
    private lateinit var productDao: ProductDao
    private var appPausedTime: Long = 0
    private var appInBackground = false
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appDatabase = AppDatabase.getDatabase(this)

        productDao = appDatabase.productDao()
        repository = ProductRepository(productDao, appDatabase)
        productViewModel = ProductViewModel(repository)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer1, ListCategoriesFragment())
            replace(R.id.fragmentContainer2, ListProductsFragment())
            commit()
        }

        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(this, AddProductActivity::class.java)

            intent.putExtra(AddProductActivity.Title, "Add Product")
            this.startActivity(intent)
        }

        productViewModel._reopenEvent.observe(this) { reopen ->
            Log.d("reopen", "onViewCreated: $reopen")
        }
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
            if (elapsedTime > (5 * 1000)) {
                Log.d("reopen", "da qua 2 minutes")
                productViewModel.setReopenEvent(true)
            }
        }
        appInBackground = false
    }
}