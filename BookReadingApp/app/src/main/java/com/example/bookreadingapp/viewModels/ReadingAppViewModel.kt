package com.example.bookreadingapp.viewModels

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ReadingAppViewModel : ViewModel() {
    var readingMode by mutableStateOf(false)

    fun toggleReadingMode() {
        readingMode = !readingMode
    }
}