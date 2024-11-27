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

class SubChaptersRepository(private val dao: SubChaptersDao) {
    val searchResults = MutableLiveData<List<SubChapters>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertSubChapter(newSubChapter: SubChapters){
        coroutineScope.launch(Dispatchers.IO) {
            dao.insertSubChapter(newSubChapter)
        }
    }

    fun findSubChapterId(subchapterId: Int): Deferred<List<SubChapters>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findSubChapterId(subchapterId)
        }

    fun findSubChapterTitle(subchapterTitle: String): Deferred<List<SubChapters>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findSubChapterTitle(subchapterTitle)
        }

    fun findSubChaptersOfChapter(chapterId: Int): Deferred<LiveData<List<SubChapters>>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.getAllSubChapters(chapterId)
        }
}