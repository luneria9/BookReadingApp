package com.example.bookreadingapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookreadingapp.data.entities.Images
import com.example.bookreadingapp.data.entities.Pages
// referenced from https://gitlab.com/crdavis/roomdatabasedemoproject
@Dao
interface ImagesDao {
    @Insert
    fun insertImage(images: Images)

    @Query("SELECT * FROM images WHERE image_id = :imageId")
    fun findImage(imageId: Int): List<Images>

    @Query("SELECT * FROM images WHERE pages_id = :pageId")
    fun getAllImages(pageId: Int): List<Images>

    @Insert
    fun insertImageAwait(images: Images): Long
}