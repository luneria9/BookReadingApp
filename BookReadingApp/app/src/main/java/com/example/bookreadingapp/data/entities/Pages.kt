package com.example.bookreadingapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "pages",
    foreignKeys = [ForeignKey(
        entity = SubChapters::class,
        childColumns = ["subchapter_id"],
        parentColumns = ["subchapter_id"]
    )])
class Pages {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pages_id")
    var id: Int = 0

    @ColumnInfo(name = "subchapter_id")
    var subchapterId: Int = 0

    @ColumnInfo(name = "page_number")
    var pageNumber: Int = 0

    @ColumnInfo(name = "contents")
    var contents: String = ""

    constructor()

    constructor(subchapterId: Int, pageNumber: Int, contents: String) {
        this.subchapterId = subchapterId
        this. pageNumber = pageNumber
        this.contents = contents
    }
}