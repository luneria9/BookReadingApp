package com.example.bookreadingapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bookreadingapp.data.dao.SubChaptersDao
import com.example.bookreadingapp.data.entities.SubChapters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
// referenced from https://gitlab.com/crdavis/roomdatabasedemoproject
class SubChaptersRepository(private val dao: SubChaptersDao) {
    val searchResults = MutableLiveData<List<SubChapters>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertSubChapter(newSubChapter: SubChapters){
        coroutineScope.launch(Dispatchers.IO) {
            dao.insertSubChapter(newSubChapter)
        }
    }

    fun findSubChapterId(id: Int) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindSubChapterId(id).await()
        }
    }

    fun findSubChapterTitle(title: String) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindSubChapterTitle(title).await()
        }
    }

    fun findSubChaptersOfChapter(id: Int) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindSubChaptersOfChapter(id).await()
        }
    }

    fun asyncfindSubChapterId(subchapterId: Int): Deferred<List<SubChapters>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findSubChapterId(subchapterId)
        }

    fun asyncfindSubChapterTitle(subchapterTitle: String): Deferred<List<SubChapters>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findSubChapterTitle(subchapterTitle)
        }

    fun asyncfindSubChaptersOfChapter(chapterId: Int): Deferred<List<SubChapters>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.getAllSubChapters(chapterId)
        }
}