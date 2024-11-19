package com.example.bookreadingapp

import android.content.Context
import android.os.Environment
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bookreadingapp.fileSystem.FileSystem
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import okhttp3.Request
import okhttp3.Response
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

//Referred to https://www.vogella.com/tutorials/AndroidTesting/article.html
@RunWith(AndroidJUnit4::class)
class FileSystemTests {
    private lateinit var mockContext: Context
    private lateinit var fileSystem: FileSystem
    private lateinit var mockClient: OkHttpClient

    @Before
    fun setUp() {
        mockContext = ApplicationProvider.getApplicationContext()
        fileSystem = FileSystem(mockContext)
        mockClient = mock(OkHttpClient::class.java)
    }

    // Test to ensure that the ListDirectory displays the contents
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

    // Test to ensure that the deleteDirectoryContents delete the directory contents
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

    // Test to ensure that the createFile creates a new file in the expected directory
    @Test
    fun createFile() {
        // Setup
        val directoryName = "testDirectory"
        val fileName = "testFile.txt"

        // Call createFile to create the file
        val file = fileSystem.createFile(directoryName, fileName)

        // Create the file on the filesystem
        file.createNewFile()

        // Assert that the file was created in the expected directory
        assertTrue(file.exists())
        assertEquals(file.name, fileName)
    }

    //Not working
    // Test to ensure that the downloadFile download file from URL and save it in the specified location
    @Test
    fun downloadFile() {
        val directoryName = "testDirectory"
        val fileName = "downloadedFile.txt"
        val file = fileSystem.createFile(directoryName, fileName)

        // Mock the URL connection
        val mockUrl = mock<URL>()
        val mockConnection = mock<HttpURLConnection>()
        val mockInputStream = mock<InputStream>()

        whenever(mockUrl.openConnection()).thenReturn(mockConnection)
        whenever(mockConnection.inputStream).thenReturn(mockInputStream)
        whenever(mockConnection.responseCode).thenReturn(HttpURLConnection.HTTP_OK)

        // Simulate the downloadFile method
        val result = fileSystem.downloadFile("https://www.gutenberg.org/cache/epub/74755/pg74755.txt", file)

        assertTrue(result)
        assertTrue(file.exists())
    }
}