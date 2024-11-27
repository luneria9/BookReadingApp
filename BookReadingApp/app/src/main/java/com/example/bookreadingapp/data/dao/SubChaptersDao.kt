package com.example.bookreadingapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookreadingapp.data.entities.SubChapters

@Dao
interface SubChaptersDao {
    @Insert
    fun insertSubChapter(subChapters: SubChapters)

    @Query("SELECT * FROM subchapters WHERE subchapter_id = :subchapterId")
    fun findSubChapter(subchapterId: Int): List<SubChapters>

    @Query("SELECT * FROM subchapters WHERE chapter_id = :chapterId")
    fun getAllSubChapters(chapterId: Int): LiveData<List<SubChapters>>
}