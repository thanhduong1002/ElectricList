package com.example.electroniclist

import android.app.AlertDialog
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.example.electroniclist.data.Products
import com.example.electroniclist.data.local.AppDatabase
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.repository.ProductRepository
import com.example.electroniclist.databinding.ActivityAddProductBinding
import com.example.electroniclist.databinding.ActivityDetailProductBinding
import com.example.electroniclist.retrofit.ServiceInterface
import com.example.electroniclist.viewmodel.ProductViewModel

class AddProductActivity : AppCompatActivity(), ProductCallback {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var textName: EditText
    private lateinit var priceProduct: EditText
    private lateinit var ratingProduct: EditText
    private lateinit var descripProduct: EditText
    private lateinit var spinner: Spinner
    private lateinit var repository: ProductRepository
    private lateinit var productDao: ProductDao
    private lateinit var serviceInterface: ServiceInterface
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
        spinner = findViewById(R.id.spinner)
        spinner.adapter = adapter

        val btnAdd: Button = findViewById(R.id.buttonAdd)
        if (productId != null) {
            productViewModel.getDetailProduct(productId, this)
            btnAdd.text = "Update Product"
        } else btnAdd.text = "Add Product"

        textName = findViewById(R.id.editName)
        priceProduct = findViewById(R.id.editPrice)
        ratingProduct = findViewById(R.id.editRating)
        descripProduct = findViewById(R.id.editDescrip)

        btnAdd.setOnClickListener {
            onClickAddButton(productId)
        }
    }

    companion object {
        val Title = "title"
        val titleEdit = "titleEdit"
        val ProductId = "ProductId"
    }

    override fun onProductReceived(product: Products?) {
        val productName: String? = product?.title
        val editableProductName: Editable = Editable.Factory.getInstance().newEditable(productName)

        val productPrice: Int? = product?.price
        val productPriceString: String? = productPrice.toString()
        val editableProductPrice: Editable = Editable.Factory.getInstance().newEditable(productPriceString)

        val productRating: Double? = product?.rating
        val productRatingString: String? = productRating.toString()
        val editableProductRating: Editable = Editable.Factory.getInstance().newEditable(productRatingString)

        val productDescrip: String? = product?.description
        val editableProductDescrip: Editable = Editable.Factory.getInstance().newEditable(productDescrip)

        if (product != null) {
            textName.text = editableProductName
            priceProduct.text = editableProductPrice
            ratingProduct.text = editableProductRating
            descripProduct.text = editableProductDescrip
            when(product?.category){
                "smartphones" -> spinner.setSelection(0)
                "laptops" -> spinner.setSelection(1)
                "fragrances" -> spinner.setSelection(2)
                "skincare" -> spinner.setSelection(3)
                "groceries" -> spinner.setSelection(4)
                "home-decoration" -> spinner.setSelection(5)
                "furniture" -> spinner.setSelection(6)
                "tops" -> spinner.setSelection(7)
                "womens-dresses" -> spinner.setSelection(8)
                "womens-shoes" -> spinner.setSelection(9)
                "mens-shirts" -> spinner.setSelection(10)
                "mens-shoes" -> spinner.setSelection(11)
                "mens-watches" -> spinner.setSelection(12)
                "womens-watches" -> spinner.setSelection(13)
                "womens-bags" -> spinner.setSelection(14)
                "womens-jewellery" -> spinner.setSelection(15)
                "sunglasses" -> spinner.setSelection(16)
                "automotive" -> spinner.setSelection(17)
                "motorcycle" -> spinner.setSelection(18)
                else -> spinner.setSelection(19)
            }
        }
    }

    override fun onApiFailure(errorMessage: String) {
        val dialog = AlertDialog.Builder(this)
        val textView = TextView(this)

        with(textView) {
            textView.text = "Our alert"
            textView.textSize = 20.0F
            textView.setTypeface(null, Typeface.BOLD)
            textView.gravity = Gravity.CENTER
        }

        dialog.setMessage("The product download has encountered an error.")
        dialog.setNeutralButton("Dismiss") { dialog, which ->
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun onClickAddButton(productId: String?) {
        val inputNameProduct = textName.text.toString()

        val stringPrice = priceProduct.text.toString()
        val inputPriceProduct: Int = stringPrice.toIntOrNull() ?: 0

        val stringRating = ratingProduct.text.toString()
        val inputRatingProduct: Double = stringRating.toDoubleOrNull() ?: 0.0

        val inputDescripProduct = descripProduct.text.toString()

        val inputCategory = spinner.selectedItem.toString()

        val newProduct = Products(
            title = inputNameProduct,
            price = inputPriceProduct,
            rating = inputRatingProduct,
            description = inputDescripProduct,
            category = inputCategory,
        )

        val dialog = AlertDialog.Builder(this)
        val textView = TextView(this)

        with(textView) {
            textView.text = "Our alert"
            textView.textSize = 20.0F
            textView.setTypeface(null, Typeface.BOLD)
            textView.gravity = Gravity.CENTER
        }

        dialog.setCustomTitle(textView)

        if (inputNameProduct.trim().isEmpty() || inputDescripProduct.trim().isEmpty()) {
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

        dialog.setNeutralButton("Dismiss") { dialog, which ->
            dialog.dismiss()
        }

        dialog.show()
    }
}