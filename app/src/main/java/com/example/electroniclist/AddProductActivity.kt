package com.example.electroniclist

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.electroniclist.data.Products
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.databinding.ActivityAddProductBinding
import com.example.electroniclist.viewmodel.ProductViewModel

class AddProductActivity : AppCompatActivity(), ProductCallback {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var repository: ProductRepository
    private lateinit var productDao: ProductDao

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appDatabase = AppDatabase.getDatabase(this)

        productDao = appDatabase.productDao()

        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent?.extras?.getString(ProductId)
        val title = intent?.extras?.getString(Title)
        val titleEdit = intent?.extras?.getString(titleEdit)
        Log.d("titleEdit", "$titleEdit")

        if (titleEdit == null) supportActionBar!!.title = title
        else supportActionBar!!.title = titleEdit

        repository = ProductRepository(productDao, appDatabase)
        productViewModel = ProductViewModel(repository)
//        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
//        repository = ProductRepository(productDao, serviceInterface)
//        productViewModel = ViewModelProvider(this, ProductViewModelFactory(repository)).get(ProductViewModel::class.java)

        val adapter = ArrayAdapter.createFromResource(this, R.array.category_list, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        if (productId != null) {
            productViewModel.getDetailProduct(productId, this)
            binding.buttonAdd.text = "Update Product"
        } else binding.buttonAdd.text = "Add Product"

        binding.buttonAdd.setOnClickListener {
            onClickAddButton(productId)
        }
    }

    companion object {
        const val Title = "title"
        const val titleEdit = "titleEdit"
        const val ProductId = "ProductId"
    }

    override fun onProductReceived(product: Products?) {
        val productName: String? = product?.title
        val editableProductName: Editable = Editable.Factory.getInstance().newEditable(productName)
        val productPrice: Int? = product?.price
        val productPriceString: String = productPrice.toString()
        val editableProductPrice: Editable = Editable.Factory.getInstance().newEditable(productPriceString)
        val productRating: Double? = product?.rating
        val productRatingString: String = productRating.toString()
        val editableProductRating: Editable = Editable.Factory.getInstance().newEditable(productRatingString)
        val productDescription: String? = product?.description
        val editableProductDescription: Editable = Editable.Factory.getInstance().newEditable(productDescription)

        if (product != null) {
            binding.editName.text = editableProductName
            binding.editPrice.text = editableProductPrice
            binding.editRating.text = editableProductRating
            binding.editDescrip.text = editableProductDescription

            when(product.category){
                "smartphones" -> binding.spinner.setSelection(0)
                "laptops" -> binding.spinner.setSelection(1)
                "fragrances" -> binding.spinner.setSelection(2)
                "skincare" -> binding.spinner.setSelection(3)
                "groceries" -> binding.spinner.setSelection(4)
                "home-decoration" -> binding.spinner.setSelection(5)
                "furniture" -> binding.spinner.setSelection(6)
                "tops" -> binding.spinner.setSelection(7)
                "womens-dresses" -> binding.spinner.setSelection(8)
                "womens-shoes" -> binding.spinner.setSelection(9)
                "mens-shirts" -> binding.spinner.setSelection(10)
                "mens-shoes" -> binding.spinner.setSelection(11)
                "mens-watches" -> binding.spinner.setSelection(12)
                "womens-watches" -> binding.spinner.setSelection(13)
                "womens-bags" -> binding.spinner.setSelection(14)
                "womens-jewellery" -> binding.spinner.setSelection(15)
                "sunglasses" -> binding.spinner.setSelection(16)
                "automotive" -> binding.spinner.setSelection(17)
                "motorcycle" -> binding.spinner.setSelection(18)
                else -> binding.spinner.setSelection(19)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onApiFailure(errorMessage: String) {
        val dialog = AlertDialog.Builder(this)
        val textView = TextView(this)

        textView.text = "Our alert"
        textView.textSize = 20.0F
        textView.setTypeface(null, Typeface.BOLD)
        textView.gravity = Gravity.CENTER

        dialog.setMessage("The product download has encountered an error.")
        dialog.setNeutralButton("Dismiss") { dialog, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun onClickAddButton(productId: String?) {
        val inputNameProduct = binding.editName.text.toString()
        val stringPrice = binding.editPrice.text.toString()
        val inputPriceProduct: Int = stringPrice.toIntOrNull() ?: 0
        val stringRating = binding.editRating.text.toString()
        val inputRatingProduct: Double = stringRating.toDoubleOrNull() ?: 0.0
        val inputDescriptionProduct = binding.editDescrip.text.toString()
        val inputCategory = binding.spinner.selectedItem.toString()
        val newProduct = Products(
            title = inputNameProduct,
            price = inputPriceProduct,
            rating = inputRatingProduct,
            description = inputDescriptionProduct,
            category = inputCategory,
        )
        val dialog = AlertDialog.Builder(this)
        val textView = TextView(this)

        textView.text = "Our alert"
        textView.textSize = 20.0F
        textView.setTypeface(null, Typeface.BOLD)
        textView.gravity = Gravity.CENTER

        dialog.setCustomTitle(textView)

        if (inputNameProduct.trim().isEmpty() || inputDescriptionProduct.trim().isEmpty()) {
            dialog.setMessage("Please enter all fields")
        } else {
            val successMessage = if (productId != null) "Update product successfully" else "Add new product successfully"

            if (productId != null) {
                productViewModel.updateProduct(productId, newProduct)
            } else {
                productViewModel.addProduct(newProduct)
            }

            dialog.setMessage(successMessage)
        }

        dialog.setNeutralButton("Dismiss") { dialog, _ ->
            dialog.dismiss()
        }

        dialog.show()
    }
}