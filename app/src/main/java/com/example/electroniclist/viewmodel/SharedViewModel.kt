package com.example.electroniclist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val productViewModel = MutableLiveData<ProductViewModel>()
}