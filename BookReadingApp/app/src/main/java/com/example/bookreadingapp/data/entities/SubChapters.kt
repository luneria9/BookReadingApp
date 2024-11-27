package com.example.bookreadingapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "subchapters",
    foreignKeys = [ForeignKey(
        entity = Chapters::class,
        childColumns = ["chapter_id"],
        parentColumns = ["chapter_id"]
    )])
class SubChapters {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subchapter_id")
    var id: Int = 0

    @ColumnInfo(name = "chapter_id")
    var chapterId: Int = 0

    @ColumnInfo(name = "subchapter_title")
    var title: String = ""

    constructor()

    constructor(title: String, chapterId: Int) {
        this.title = title
        this.chapterId = chapterId
    }
}