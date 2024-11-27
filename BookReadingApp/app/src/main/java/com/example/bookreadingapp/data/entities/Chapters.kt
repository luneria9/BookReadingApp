package com.example.bookreadingapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "chapters",
    foreignKeys = [ForeignKey(
        entity = Books::class,
        childColumns = ["book_id"],
        parentColumns = ["book_id"]
    )])
class Chapters {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "chapter_id")
    var id: Int = 0

    @ColumnInfo(name = "book_id")
    var bookId: Int = 0

    @ColumnInfo(name = "chapter_title")
    var title: String = ""

    constructor()

    constructor(title: String, bookId: Int) {
        this.title = title
        this.bookId = bookId
    }
}