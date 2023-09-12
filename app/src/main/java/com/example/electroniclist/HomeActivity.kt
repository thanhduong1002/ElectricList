package com.example.electroniclist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {
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
    }
}