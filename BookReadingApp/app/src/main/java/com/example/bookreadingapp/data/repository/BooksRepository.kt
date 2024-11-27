package com.example.bookreadingapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bookreadingapp.data.dao.BooksDao
import com.example.bookreadingapp.data.entities.Books
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BooksRepository (private val dao: BooksDao){
    val allBooks: LiveData<List<Books>> = dao.getAllBooks()
    val searchResults = MutableLiveData<List<Books>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertBook(newBook: Books){
        coroutineScope.launch(Dispatchers.IO) {
            dao.insertBook(newBook)
        }
    }

    fun findBookId(bookId: Int): Deferred<List<Books>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findBookId(bookId)
        }

    fun findBookTitle(bookTitle: String): Deferred<List<Books>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findBookTitle(bookTitle)
        }
}