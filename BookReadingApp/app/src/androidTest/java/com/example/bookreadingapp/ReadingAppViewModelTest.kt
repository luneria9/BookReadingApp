package com.example.bookreadingapp

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.bookreadingapp.fileSystem.FileSystem
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

// Referred to https://github.com/google-developer-training/basic-android-kotlin-compose-training-unscramble/blob/main/app/src/test/java/com/example/unscramble/ui/test/GameViewModelTest.kt
@OptIn(ExperimentalCoroutinesApi::class)
class ReadingAppViewModelTest {
    private lateinit var context: Context
    private lateinit var application: Application
    private lateinit var fileSystem: FileSystem
    private lateinit var viewModel: ReadingAppViewModel

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        application = ApplicationProvider.getApplicationContext()
        fileSystem = mock(FileSystem::class.java)
        viewModel = ReadingAppViewModel(fileSystem, application)
    }

    // Test to ensure that the ReadingMode toggles correctly
    @Test
    fun readingAppViewModel_ReadingMode_TogglesCorrectly() {
        assertEquals(false, viewModel.readingMode)

        viewModel.toggleReadingMode()
        assertEquals(true, viewModel.readingMode)

        viewModel.toggleReadingMode()
        assertEquals(false, viewModel.readingMode)
    }
}