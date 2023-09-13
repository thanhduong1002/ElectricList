package com.example.electroniclist.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.electroniclist.R
import com.example.electroniclist.viewmodel.ProductViewModel

class CategoryListAdapter(var categoryList: List<String>,
                          private val productViewModel: ProductViewModel): RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder>() {
    private var selectedItemPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryListAdapter.CategoryListViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryListAdapter.CategoryListViewHolder(adapterLayout)
    }

    class CategoryListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val btnCategory: Button = view.findViewById(R.id.categoryButton)
    }

    override fun onBindViewHolder(
        holder: CategoryListAdapter.CategoryListViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = categoryList[position]

        holder.btnCategory.text = item
        holder.btnCategory.setOnClickListener {
            if (position == selectedItemPosition) {
                productViewModel.selectCategory("all")
                selectedItemPosition = -1
            } else {
                productViewModel.selectCategory(item)
                Log.d("item", "$item")
                selectedItemPosition = position
                Log.d("CategoryInCateAdapter", "${productViewModel.selectedCategory.value}")
            }
            notifyDataSetChanged()
        }

        if (position == selectedItemPosition) {
            holder.btnCategory.setBackgroundColor(Color.BLUE)
        } else {
            holder.btnCategory.setBackgroundColor(Color.parseColor("#70A7D8"))
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

}