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

    fun findPageId(id: Int) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindPageId(id).await()
        }
    }

    fun findPageNumber(pageNumber: Int) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindPageNumber(pageNumber).await()
        }
    }

    fun findPagesOfSubchapter(subchapterId: Int) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindPagesOfSubchapter(subchapterId).await()
        }
    }

    fun asyncfindPageId(pageId: Int): Deferred<List<Pages>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findPageId(pageId)
        }

    fun asyncfindPageNumber(pageNumber: Int): Deferred<List<Pages>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findPageNumber(pageNumber)
        }

    fun asyncfindPagesOfSubchapter(subchapterId: Int): Deferred<List<Pages>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.getAllPages(subchapterId)
        }
}