package com.example.bookreadingapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bookreadingapp.data.dao.BooksDao
import com.example.bookreadingapp.data.dao.ChaptersDao
import com.example.bookreadingapp.data.dao.ImagesDao
import com.example.bookreadingapp.data.dao.PagesDao
import com.example.bookreadingapp.data.dao.SubChaptersDao
import com.example.bookreadingapp.data.entities.Books
import com.example.bookreadingapp.data.entities.Chapters
import com.example.bookreadingapp.data.entities.Images
import com.example.bookreadingapp.data.entities.Pages
import com.example.bookreadingapp.data.entities.SubChapters

// referenced from https://gitlab.com/crdavis/roomdatabasedemoproject
@Database (entities = [(Books::class), (Chapters::class), (SubChapters::class), (Pages::class), (Images::class)], version = 1)
abstract class ReadingRoomDatabase : RoomDatabase(){
    abstract fun booksDao(): BooksDao
    abstract fun chaptersDao(): ChaptersDao
    abstract fun subchaptersDao(): SubChaptersDao
    abstract fun pagesDao(): PagesDao
    abstract fun imagesDao(): ImagesDao
    companion object {
        @Volatile
        private var INSTANCE: ReadingRoomDatabase? = null

        fun getInstance(context: Context): ReadingRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ReadingRoomDatabase::class.java,
                        "reading_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}