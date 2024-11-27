package com.example.bookreadingapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "images",
    foreignKeys = [ForeignKey(
        entity = SubChapters::class,
        childColumns = ["page_id"],
        parentColumns = ["page_id"]
    )])
class Images {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "image_id")
    var id: Int = 0

    @ColumnInfo(name = "page_id")
    var pageId: Int = 0

    @ColumnInfo(name = "image_url")
    var imageUrl: String = ""

    constructor()

    constructor(pageId: Int, imageUrl: String) {
        this.pageId = pageId
        this.imageUrl = imageUrl
    }
}