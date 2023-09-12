package com.example.electroniclist.application

import android.app.Application
import com.example.electroniclist.data.local.AppDatabase

class BaseApplication : Application() {
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
}
