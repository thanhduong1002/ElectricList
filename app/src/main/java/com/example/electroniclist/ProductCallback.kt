package com.example.electroniclist

import com.example.electroniclist.data.Products

interface ProductCallback {
    fun onProductReceived(product: Products?)
    fun onApiFailure(errorMessage: String)
}

interface ProductAdapterListener {
    fun onDeleteProduct(productId: String)
}
