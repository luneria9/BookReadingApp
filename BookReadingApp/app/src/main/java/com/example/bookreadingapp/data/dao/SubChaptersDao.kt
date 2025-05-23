package com.example.bookreadingapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookreadingapp.data.entities.SubChapters

// referenced from https://gitlab.com/crdavis/roomdatabasedemoproject
@Dao
interface SubChaptersDao {
    @Insert
    fun insertSubChapter(subChapters: SubChapters)

    @Query("SELECT * FROM subchapters WHERE subchapter_id = :subchapterId")
    fun findSubChapterId(subchapterId: Int): List<SubChapters>

    @Query("SELECT * FROM subchapters WHERE subchapter_title = :subChapterTitle")
    fun findSubChapterTitle(subChapterTitle: String): List<SubChapters>

    @Query("SELECT * FROM subchapters WHERE chapter_id = :chapterId")
    fun getAllSubChapters(chapterId: Int): List<SubChapters>

    @Query("""
        SELECT * FROM subchapters 
        WHERE (subchapter_title LIKE '%' || :query || '%' )
        AND chapter_id IN (
            SELECT chapter_id FROM chapters 
            WHERE book_id = :bookId
        )
    """)
    fun searchSubChapters(query: String, bookId: Int): List<SubChapters>

    @Insert
    fun insertSubChapterAwait(subChapters: SubChapters): Long
}