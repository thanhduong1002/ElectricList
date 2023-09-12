package com.example.electroniclist.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.electroniclist.data.StringListConverter
import com.example.electroniclist.data.local.dao.ProductDao
import com.example.electroniclist.data.local.entities.ProductEntity
import com.example.electroniclist.viewmodel.ProductViewModel

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context) = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build().apply {
                INSTANCE = this
            }
        }
    }
}