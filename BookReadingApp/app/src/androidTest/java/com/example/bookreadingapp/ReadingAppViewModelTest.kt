package com.example.bookreadingapp

import android.content.Context
import com.example.bookreadingapp.fileSystem.FileSystem
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

// For testing
class FakeFileSystem(context: Context?) : FileSystem(context!!) {
}

// Referred to https://github.com/google-developer-training/basic-android-kotlin-compose-training-unscramble/blob/main/app/src/test/java/com/example/unscramble/ui/test/GameViewModelTest.kt
@OptIn(ExperimentalCoroutinesApi::class)
class ReadingAppViewModelTest {
    private val fakeFileSystem = FakeFileSystem(context = null)
    private val viewModel = ReadingAppViewModel(fakeFileSystem)

    @Test
    fun readingAppViewModel_ReadingMode_TogglesCorrectly() {
        assertEquals(false, viewModel.readingMode)

        viewModel.toggleReadingMode()
        assertEquals(true, viewModel.readingMode)

        viewModel.toggleReadingMode()
        assertEquals(false, viewModel.readingMode)
    }
}