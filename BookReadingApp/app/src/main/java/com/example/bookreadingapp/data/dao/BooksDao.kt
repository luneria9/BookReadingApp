package com.example.bookreadingapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookreadingapp.data.entities.Books
// referenced from https://gitlab.com/crdavis/roomdatabasedemoproject
@Dao
interface BooksDao {
    @Insert
    fun insertBook(book: Books)

    @Query("SELECT * FROM books WHERE book_id = :bookId")
    fun findBookId(bookId: Int): List<Books>

    @Query("SELECT DISTINCT * FROM books WHERE title = :bookTitle")
    fun findBookTitle(bookTitle: String): List<Books>

    @Query("SELECT * FROM books")
    fun getAllBooks(): LiveData<List<Books>>

    @Insert
    fun insertBookAwait(book: Books) : Long
}