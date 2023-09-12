package com.example.electroniclist.adapter

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.electroniclist.AddProductActivity
import com.example.electroniclist.DetailProductActivity
import com.example.electroniclist.ProductAdapterListener
import com.example.electroniclist.R
import com.example.electroniclist.data.Products
import com.example.electroniclist.viewmodel.ProductViewModel
import com.squareup.picasso.Picasso

class ElectricListAdapter(var electricList: List<Products>) :
    RecyclerView.Adapter<ElectricListAdapter.ElectricListHolder>() {

    private var listener: ProductAdapterListener? = null

    fun setListener(listener: ProductAdapterListener) {
        this.listener = listener
    }
    fun setProductsList(electricList: List<Products>) {
        this.electricList = electricList
    }

    class ElectricListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameItem: TextView = view.findViewById(R.id.textViewName)
        val priceItem: TextView = view.findViewById(R.id.textViewPrice)
        val imageItem: ImageView = view.findViewById(R.id.imageItem)
        val item: ConstraintLayout = view.findViewById(R.id.item)
        val editItem: ImageButton = view.findViewById(R.id.editButton)
        val deleteItem: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectricListHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.electric_item, parent, false)
        return ElectricListHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return electricList.size
    }

    override fun onBindViewHolder(holder: ElectricListHolder, position: Int) {
        val item = electricList[position]

        holder.nameItem.text = item?.title.toString()
        holder.priceItem.text = "$" + item?.price.toString()
        Picasso.get().load(item?.images?.get(0)).into(holder.imageItem)

        holder.item.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailProductActivity::class.java)
            intent.putExtra(DetailProductActivity.ProductId, item?.id.toString())
            intent.putExtra(DetailProductActivity.TitleProduct, item?.title)
            context.startActivity(intent)
        }

        holder.editItem.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AddProductActivity::class.java)
            intent.putExtra(AddProductActivity.titleEdit, item?.title)
            intent.putExtra(AddProductActivity.ProductId, item?.id.toString())
            context.startActivity(intent)
        }

        holder.deleteItem.setOnClickListener {
            val dialog = AlertDialog.Builder(holder.itemView.context)
            val textView = TextView(holder.itemView.context)

            with(textView) {
                text = "Our alert"
                textSize = 20.0F
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
            }

            dialog.setMessage("Are you sure you want to delete this product?")
            dialog.setPositiveButton("Yes") { dialog, which ->
                listener?.onDeleteProduct(item?.id.toString())
                notifyDataSetChanged()
                dialog.dismiss()
            }

            dialog.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            dialog.show()
        }
    }

}