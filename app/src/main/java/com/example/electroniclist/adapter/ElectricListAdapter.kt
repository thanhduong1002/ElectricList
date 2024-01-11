package com.example.electroniclist.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.electroniclist.AddProductActivity
import com.example.electroniclist.DetailProductFragment
import com.example.electroniclist.ProductAdapterListener
import com.example.electroniclist.data.Products
import com.example.electroniclist.databinding.ElectricItemBinding
import com.squareup.picasso.Picasso

class ElectricListAdapter(private var electricList: List<Products>) :
    RecyclerView.Adapter<ElectricListAdapter.ElectricListHolder>() {

    private var listener: ProductAdapterListener? = null
    fun setListener(listener: ProductAdapterListener) {
        this.listener = listener
    }

    fun setProductsList(electricList: List<Products>) {
        this.electricList = electricList
    }

    class ElectricListHolder(val electricItemBinding: ElectricItemBinding) :
        RecyclerView.ViewHolder(electricItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectricListHolder {
        return ElectricListHolder(
            ElectricItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return electricList.size
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ElectricListHolder, position: Int) {
        val item = electricList[position]

        holder.electricItemBinding.apply {
            textViewName.text = item.title.toString()
            textViewPrice.text = "$${item.price}"
            textViewId.text = item.id.toString()
            item.images?.get(0).let {
                Picasso.get().load(it).into(imageItem)
            }

            productItem.setOnClickListener {
                val args = Bundle().apply {
                    putString(DetailProductFragment.ProductId, item.id.toString())
                    putString(DetailProductFragment.TitleProduct, item.title)
                    putString(DetailProductFragment.DescriptionProduct, item.description)
                    putString(DetailProductFragment.ProductRating, item.rating.toString())
                    putString(DetailProductFragment.ProductPrice, item.price.toString())
                    putString(DetailProductFragment.ProductImage, item.images.toString())
                }
                item.id?.let { it1 -> listener?.onClickDetailProduct(it1, args) }
            }

            editButton.setOnClickListener {
                val context = holder.itemView.context

                Intent(context, AddProductActivity::class.java).apply {
                    putExtra(AddProductActivity.titleEdit, item.title)
                    putExtra(AddProductActivity.ProductId, item.id.toString())
                }.run {
                    context.startActivity(this)
                }
            }

            deleteButton.setOnClickListener {
                val textView = TextView(holder.itemView.context)

                with(textView) {
                    text = "Our alert"
                    textSize = 20.0F
                    setTypeface(null, Typeface.BOLD)
                    gravity = Gravity.CENTER
                }

                AlertDialog.Builder(holder.itemView.context).apply {
                    setMessage("Are you sure you want to delete this product?")

                    setPositiveButton("Yes") { dialog, _ ->
                        listener?.onDeleteProduct(item.id.toString())
                        notifyDataSetChanged()
                        dialog.dismiss()
                    }

                    setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.show()
            }
        }
    }
}