package com.example.bookreadingapp.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookreadingapp.fileSystem.FileSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReadingAppViewModel(private val fileSystem: FileSystem) : ViewModel() {
    private val _directoryContents = MutableLiveData<List<String>>()
    val directoryContents: LiveData<List<String>> = _directoryContents

    var readingMode by mutableStateOf(false)

    fun toggleReadingMode() {
        readingMode = !readingMode
    }

    // from https://gitlab.com/crdavis/networkandfileio/-/tree/master?ref_type=heads
    // Function to set up file download
    fun setupDownload(url: String) {
            val fileName = url.substringAfterLast("/")
            val file = fileSystem.createFile("DownloadedFiles", fileName)

            if (fileSystem.downloadFile(url, file)) {
                updateDirectoryContents("DownloadedFiles")
            } else {
                Log.e("DownloadViewModel", "Failed to download file")
            }
    }

    fun unzipFile(fileName: String, destDirectory: String) {
            if (fileSystem.unzipFile(fileName, destDirectory, "DownloadedFiles")) {
                updateDirectoryContents("UnzippedBooks")
            } else {
                Log.e("DownloadViewModel", "Failed to unzip $fileName")
            }
    }

    fun downloadUnzip(url: String, fileName: String, destDirectory: String) {
        viewModelScope.launch(Dispatchers.Default){
            val downloadJob = launch(Dispatchers.IO) { setupDownload(url) }
            downloadJob.join()
            val unzipJob = launch(Dispatchers.Default) { unzipFile(fileName, destDirectory) }
        }
    }

    private fun updateDirectoryContents(directoryName: String) {
        val contents = fileSystem.listDirectoryContents(directoryName)
        _directoryContents.postValue(contents)
    }

    fun confirmDeletion(directoryName: String) {
        fileSystem.deleteDirectoryContents(directoryName)
        updateDirectoryContents(directoryName)
    }

}