package com.example.electroniclist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.databinding.ActivityHomeBinding
import com.example.electroniclist.viewmodel.ProductViewModel
import com.example.electroniclist.viewmodel.SharedViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var repository: ProductRepository
    private lateinit var productDao: ProductDao
    private var appPausedTime: Long = 0
    private var appInBackground = false
    private lateinit var binding: ActivityHomeBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBarTitle(getString(R.string.app_name))

        val appDatabase = AppDatabase.getDatabase(this)

        productDao = appDatabase.productDao()
        repository = ProductRepository(productDao, appDatabase)
        productViewModel = ProductViewModel(repository)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        sharedViewModel.productViewModel.value = productViewModel

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer1, ListCategoriesFragment())
            replace(R.id.fragmentContainer2, ListProductsFragment())
            commit()
        }

        binding.floatingActionButton.setOnClickListener {
            Intent(this, AddProductActivity::class.java).apply {
                putExtra(AddProductActivity.Title, "Add Product")
            }.run {
                startActivity(this)
            }
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
            if (elapsedTime > (2 * 60 * 1000)) {
                Log.d("reopen", "da qua 2 minutes")
                sharedViewModel.reOpenEvent.value = true
            }
        }
        appInBackground = false

        binding.fragmentContainer1.visibility = View.VISIBLE
        setActionBarTitle(getString(R.string.app_name))
    }

    fun setActionBarTitle(title: String?) {
        supportActionBar?.title = title
    }
}