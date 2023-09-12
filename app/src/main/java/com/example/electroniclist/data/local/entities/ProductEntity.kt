package com.example.electroniclist.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.electroniclist.data.Products

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey
    val id: Int? = null,

    @ColumnInfo(name = "title")
    val title: String? = null,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "price")
    val price: Int? = null,

    @ColumnInfo(name = "discountPercentage")
    val discountPercentage: Double? = null,

    @ColumnInfo(name = "rating")
    val rating: Double? = null,

    @ColumnInfo(name = "stock")
    val stock: Int? = null,

    @ColumnInfo(name = "brand")
    val brand: String? = null,

    @ColumnInfo(name = "category")
    val category: String? = null,

    @ColumnInfo(name = "thumbnail")
    val thumbnail: String? = null,

    @ColumnInfo(name = "images")
    val images: ArrayList<String>? = null,
)

fun List<ProductEntity>.asApiResponse(): List<Products>{
    return map {
        Products(
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
