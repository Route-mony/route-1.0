package com.beyondthehorizon.route.models.contacts

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers

class MyViewModelFactory(private val application: Application, private val context: Context) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MyContactsViewModel::class.java)) {
            val source =
                MyContactsDataSource(application.contentResolver, context)
            MyContactsViewModel(application, MyContactsRepository(source, Dispatchers.IO)) as T
        } else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}
