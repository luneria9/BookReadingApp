package com.example.bookreadingapp.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookreadingapp.fileSystem.FileSystem

class ReadingAppViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReadingAppViewModel::class.java)) {
            val repository = FileSystem(context)
            return ReadingAppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}