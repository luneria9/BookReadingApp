package com.example.bookreadingapp.data.dao

import android.graphics.pdf.PdfDocument.Page
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookreadingapp.data.entities.Pages

@Dao
interface PagesDao {
    @Insert
    fun insertPage(pages: Pages)

    @Query("SELECT * FROM pages WHERE pages_id = :pageId")
    fun findPage(pageId: Int): List<Pages>

    @Query("SELECT * FROM pages WHERE subchapter_id = :subChapterId")
    fun getAllPages(subChapterId: Int): LiveData<List<Pages>>
}