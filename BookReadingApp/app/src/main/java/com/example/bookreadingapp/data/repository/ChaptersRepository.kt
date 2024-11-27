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

class ChaptersRepository(private val dao: ChaptersDao) {
    val searchResults = MutableLiveData<List<Chapters>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertChapter(newChapter: Chapters){
        coroutineScope.launch(Dispatchers.IO) {
            dao.insertChapter(newChapter)
        }
    }

    fun findChapterId(chapterId: Int): Deferred<List<Chapters>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findChapterId(chapterId)
        }

    fun findChapterTitle(chapterTitle: String): Deferred<List<Chapters>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findChapterTitle(chapterTitle)
        }

    fun findChaptersOfBook(bookId: Int): Deferred<LiveData<List<Chapters>>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.getAllChaptersFromBook(bookId)
        }
}