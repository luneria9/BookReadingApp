package com.example.bookreadingapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
// referenced from https://gitlab.com/crdavis/roomdatabasedemoproject
@Entity(tableName = "images",
    foreignKeys = [ForeignKey(
        entity = Pages::class,
        childColumns = ["pages_id"],
        parentColumns = ["pages_id"]
    )])
class Images {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "image_id")
    var id: Int = 0

    @ColumnInfo(name = "pages_id")
    var pageId: Int = 0

    @ColumnInfo(name = "image_url")
    var imageUrl: String = ""

    constructor()

    constructor(pageId: Int, imageUrl: String) {
        this.pageId = pageId
        this.imageUrl = imageUrl
    }
}