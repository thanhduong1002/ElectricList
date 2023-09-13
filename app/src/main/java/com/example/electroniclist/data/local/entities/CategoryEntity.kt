package com.example.electroniclist.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")

data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name: String
)

fun List<CategoryEntity>.asCategoryList(): List<String> {
    return map { it.name }
}

fun List<String>.asCategoryEntities(): List<CategoryEntity> {
    return map { CategoryEntity(name = it) }
}

