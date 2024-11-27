package com.example.bookreadingapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bookreadingapp.data.entities.Images
import com.example.bookreadingapp.data.entities.Pages

@Dao
interface ImagesDao {
    @Insert
    fun insertImage(images: Images)

    @Query("SELECT * FROM images WHERE image_id = :imageId")
    fun findImage(imageId: Int): List<Images>

    @Query("SELECT * FROM images WHERE page_id = :pageId")
    fun getAllImages(pageId: Int): LiveData<List<Images>>
}