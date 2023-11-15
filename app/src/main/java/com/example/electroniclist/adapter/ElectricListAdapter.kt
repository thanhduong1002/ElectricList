package com.example.electroniclist.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.electroniclist.AddProductActivity
import com.example.electroniclist.DetailProductFragment
import com.example.electroniclist.ProductAdapterListener
import com.example.electroniclist.R
import com.example.electroniclist.data.Products
import com.example.electroniclist.databinding.ElectricItemBinding
import com.squareup.picasso.Picasso

class ElectricListAdapter(private var electricList: List<Products>) :
    RecyclerView.Adapter<ElectricListAdapter.ElectricListHolder>() {

    private var listener: ProductAdapterListener? = null
    private var fragmentActivity: FragmentActivity? = null
    fun setListener(listener: ProductAdapterListener, fragmentActivity: FragmentActivity) {
        this.listener = listener
        this.fragmentActivity = fragmentActivity
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

    @Suppress("NAME_SHADOWING")
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ElectricListHolder, position: Int) {
        val item = electricList[position]

        holder.electricItemBinding.textViewName.text = item.title.toString()
        holder.electricItemBinding.textViewPrice.text = "$${item.price}"
        Picasso.get().load(item.images?.get(0)).into(holder.electricItemBinding.imageItem)

        holder.electricItemBinding.item.setOnClickListener {
            fragmentActivity?.let { activity ->
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

                val detailProductFragment = DetailProductFragment()

                val args = Bundle()
                args.putString(DetailProductFragment.ProductId, item.id.toString())
                args.putString(DetailProductFragment.TitleProduct, item.title)
                detailProductFragment.arguments = args

                val fragmentContainer1 = activity.findViewById<FrameLayout>(R.id.fragmentContainer1)

                fragmentContainer1.visibility = View.INVISIBLE
                fragmentTransaction.replace(R.id.fragmentContainer2, detailProductFragment)
                fragmentTransaction.addToBackStack(null)

                fragmentTransaction.commit()
            }
        }

        holder.electricItemBinding.editButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AddProductActivity::class.java)

            intent.putExtra(AddProductActivity.titleEdit, item.title)
            intent.putExtra(AddProductActivity.ProductId, item.id.toString())

            context.startActivity(intent)
        }

        holder.electricItemBinding.deleteButton.setOnClickListener {
            val dialog = AlertDialog.Builder(holder.itemView.context)
            val textView = TextView(holder.itemView.context)

            with(textView) {
                text = "Our alert"
                textSize = 20.0F
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
            }

            dialog.setMessage("Are you sure you want to delete this product?")
            dialog.setPositiveButton("Yes") { dialog, _ ->
                listener?.onDeleteProduct(item.id.toString())
                notifyDataSetChanged()
                dialog.dismiss()
            }

            dialog.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            dialog.show()
        }
    }

}