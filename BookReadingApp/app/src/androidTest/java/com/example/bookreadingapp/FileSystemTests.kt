package com.example.bookreadingapp

import android.content.Context
import android.os.Environment
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bookreadingapp.fileSystem.FileSystem
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

//Referred to https://www.vogella.com/tutorials/AndroidTesting/article.html
@RunWith(AndroidJUnit4::class)
class FileSystemTests {
    private lateinit var mockContext: Context
    private lateinit var fileSystem: FileSystem

    @Before
    fun setUp() {
        mockContext = ApplicationProvider.getApplicationContext()
        fileSystem = FileSystem(mockContext)
    }

    @Test
    fun listDirectoryContents() {
        // Setup
        val directoryName = "testDirectory"
        val mockDir = File(mockContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), directoryName)
        if (!mockDir.exists()) mockDir.mkdirs()
        val file1 = File(mockDir, "file1.txt")
        val file2 = File(mockDir, "file2.txt")
        file1.createNewFile()
        file2.createNewFile()

        // Test the listDirectoryContents method
        val fileList = fileSystem.listDirectoryContents(directoryName)

        // Assert that the directory contents are listed
        assertEquals(fileList.size, 2)
        assertTrue(fileList.contains("file1.txt"))
        assertTrue(fileList.contains("file2.txt"))
    }

    @Test
    fun deleteDirectoryContents() {
        // Setup
        val directoryName = "testDirectory"
        val mockDir = File(mockContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), directoryName)
        if (!mockDir.exists()) mockDir.mkdirs()
        val file1 = File(mockDir, "file1.txt")
        val file2 = File(mockDir, "file2.txt")
        file1.createNewFile()
        file2.createNewFile()

        // Test the deleteDirectoryContents method
        fileSystem.deleteDirectoryContents(directoryName)

        // Assert that the files are deleted
        assertFalse(file1.exists())
        assertFalse(file2.exists())
    }
}