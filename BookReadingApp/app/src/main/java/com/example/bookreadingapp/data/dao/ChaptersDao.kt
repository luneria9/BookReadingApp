package com.example.bookreadingapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookreadingapp.data.entities.Chapters

// referenced from https://gitlab.com/crdavis/roomdatabasedemoproject
@Dao
interface ChaptersDao {
    @Insert
    fun insertChapter(chapters: Chapters)

    @Query("SELECT * FROM chapters WHERE chapter_id = :chapterId")
    fun findChapterId(chapterId: Int): List<Chapters>

    @Query("SELECT DISTINCT * FROM chapters WHERE chapter_title = :chapterTitle")
    fun findChapterTitle(chapterTitle: String): List<Chapters>

    @Query("SELECT * FROM chapters WHERE book_id = :bookId")
    fun getAllChaptersFromBook(bookId: Int): List<Chapters>

    @Query("""
        SELECT * FROM chapters 
        WHERE (chapter_title LIKE '%' || :query || '%' )
        AND book_id = :bookId
    """)
    fun searchChapters(query: String, bookId: Int): List<Chapters>

    @Insert
    fun insertChapterAwait(chapters: Chapters): Long
}