package com.example.electroniclist.data

import com.example.electroniclist.data.local.entities.ProductEntity
import com.google.gson.annotations.SerializedName

data class ApiResponse (

    @SerializedName("products" ) var products : ArrayList<Products> = arrayListOf(),
    @SerializedName("total"    ) var total    : Int?                = null,
    @SerializedName("skip"     ) var skip     : Int?                = null,
    @SerializedName("limit"    ) var limit    : Int?                = null

)
data class Products (

    @SerializedName("id"                 ) var id                 : Int?              = null,
    @SerializedName("title"              ) var title              : String?           = null,
    @SerializedName("description"        ) var description        : String?           = null,
    @SerializedName("price"              ) var price              : Int?              = null,
    @SerializedName("discountPercentage" ) var discountPercentage : Double?           = null,
    @SerializedName("rating"             ) var rating             : Double?           = null,
    @SerializedName("stock"              ) var stock              : Int?              = null,
    @SerializedName("brand"              ) var brand              : String?           = null,
    @SerializedName("category"           ) var category           : String?           = null,
    @SerializedName("thumbnail"          ) var thumbnail          : String?           = null,
    @SerializedName("images"             ) var images             : ArrayList<String>? = null

)

fun List<Products>.asEntity(): List<ProductEntity>{
    return map {
        ProductEntity(
            id = it.id,
            title = it.title,
            description = it.description,
            price = it.price,
            discountPercentage = it.discountPercentage,
            rating = it.rating,
            stock = it.stock,
            brand = it.brand,
            category = it.category,
            thumbnail = it.thumbnail,
            images = it.images
        )
    }
}

