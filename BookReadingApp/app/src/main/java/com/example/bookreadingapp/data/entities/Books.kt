package com.example.bookreadingapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "books")
class Books {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "book_id")
    var id: Int = 0

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "author")
    var author: String = ""

    @ColumnInfo(name = "subject")
    var subject: String = ""

    @ColumnInfo(name = "release_date")
    var date: String = ""

    constructor()

    constructor(title: String, author: String, subject: String, date: String) {
        this.title = title
        this.author = author
        this.subject = subject
        this.date = date
    }
}