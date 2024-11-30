package com.example.bookreadingapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bookreadingapp.data.dao.ChaptersDao
import com.example.bookreadingapp.data.entities.Chapters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
// referenced from https://gitlab.com/crdavis/roomdatabasedemoproject
class ChaptersRepository(private val dao: ChaptersDao) {
    val searchResults = MutableLiveData<List<Chapters>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertChapter(newChapter: Chapters){
        coroutineScope.launch(Dispatchers.IO) {
            dao.insertChapter(newChapter)
        }
    }

    fun findChapterId(id: Int) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindChapterId(id).await()
        }
    }

    fun findChapterTitle(chapterTitle: String) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindChapterTitle(chapterTitle).await()
        }
    }

    fun findChaptersOfBook(id: Int) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindChaptersOfBook(id).await()
        }
    }

    fun insertChapterAsync(chapters: Chapters): Long {
        return dao.insertChapterAwait(chapters)
    }

    fun asyncfindChapterId(chapterId: Int): Deferred<List<Chapters>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findChapterId(chapterId)
        }

    fun asyncfindChapterTitle(chapterTitle: String): Deferred<List<Chapters>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findChapterTitle(chapterTitle)
        }

    fun asyncfindChaptersOfBook(bookId: Int): Deferred<List<Chapters>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.getAllChaptersFromBook(bookId)
        }
}