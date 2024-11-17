package com.example.bookreadingapp.fileSystem

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

// referenced from https://gitlab.com/crdavis/networkandfileio/-/tree/master?ref_type=heads
open class FileSystem(private val context: Context?){

    fun createFile(directoryName: String, fileName: String): File {
        val downloadFolder = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), directoryName)
        if (!downloadFolder.exists()) downloadFolder.mkdirs()
        return File(downloadFolder, fileName)
    }

    // List directory contents
    fun listDirectoryContents(directoryName: String): List<String> {
        val downloadFolder = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), directoryName)
        return downloadFolder.listFiles()?.map { it.name } ?: emptyList()
    }

    // Download file from URL and save it in the specified location
    fun downloadFile(url: String, file: File): Boolean {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful || response.body == null) {
                return false
            }

            response.body!!.byteStream().use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    copyStream(inputStream, outputStream)
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace() // Log the exception
            false
        }
    }

    // Helper method to copy data from input to output stream
    private fun copyStream(input: InputStream, output: FileOutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
    }


    // Delete directory contents directly without IntentSender
    fun deleteDirectoryContents(directoryName: String) {
        val downloadFolder = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), directoryName)
        downloadFolder.listFiles()?.forEach { it.delete() }
    }

}