package com.example.electroniclist.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.electroniclist.databinding.CategoryItemBinding
import com.example.electroniclist.interfaces.IChooseCategory

class CategoryListAdapter(
    var categoryList: List<String>,
    private val listener: IChooseCategory
) : RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder>() {
    private var selectedItemPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryListViewHolder {
        return CategoryListViewHolder(
            CategoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class CategoryListViewHolder(val categoryItemBinding: CategoryItemBinding) :
        RecyclerView.ViewHolder(categoryItemBinding.root)

    @SuppressLint("RecyclerView", "NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: CategoryListViewHolder,
        position: Int
    ) {
        val item = categoryList[position]

        holder.categoryItemBinding.categoryButton.apply {
            text = item
            setOnClickListener {
                selectedItemPosition = if (position == selectedItemPosition) {
                    listener.onClickCategory("all")
                    -1
                } else {
                    listener.onClickCategory(item)
                    position
                }

                notifyDataSetChanged()
            }

            if (position == selectedItemPosition) {
                setBackgroundColor(Color.BLUE)
            } else {
                setBackgroundColor(Color.parseColor("#70A7D8"))
            }
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}