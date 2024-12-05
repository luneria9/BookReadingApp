package com.example.bookreadingapp.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookreadingapp.data.fileSystem.FileSystem

class ReadingAppViewModelFactory(private val context: Context,
                                 private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReadingAppViewModel::class.java)) {
            val repository = FileSystem(context)
            return ReadingAppViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}