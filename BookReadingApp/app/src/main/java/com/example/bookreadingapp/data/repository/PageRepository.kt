package com.example.bookreadingapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bookreadingapp.data.dao.PagesDao
import com.example.bookreadingapp.data.entities.Pages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
// referenced from https://gitlab.com/crdavis/roomdatabasedemoproject
class PageRepository(private val dao: PagesDao) {
    val searchResults = MutableLiveData<List<Pages>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertPage(newPages: Pages){
        coroutineScope.launch(Dispatchers.IO) {
            dao.insertPage(newPages)
        }
    }

    fun findPageId(pageId: Int): Deferred<List<Pages>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findPageId(pageId)
        }

    fun findPageNumber(pageNumber: Int): Deferred<List<Pages>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findPageNumber(pageNumber)
        }

    fun findPagesOfSubchapter(subchapterId: Int): Deferred<LiveData<List<Pages>>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.getAllPages(subchapterId)
        }
}