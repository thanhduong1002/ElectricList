package com.example.electroniclist

import android.os.Bundle
import com.example.electroniclist.data.Products

interface ProductCallback {
    fun onProductReceived(product: Products?)
    fun onApiFailure(errorMessage: String)
}

interface ProductAdapterListener {
    fun onDeleteProduct(productId: String)

    fun onClickDetailProduct(productId: Int, bundle: Bundle)
}
