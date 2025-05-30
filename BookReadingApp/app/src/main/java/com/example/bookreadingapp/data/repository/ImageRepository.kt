package com.example.bookreadingapp.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.bookreadingapp.data.dao.ImagesDao
import com.example.bookreadingapp.data.entities.Images
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
// referenced from https://gitlab.com/crdavis/roomdatabasedemoproject
class ImageRepository(private val dao: ImagesDao) {
    val searchResults = MutableLiveData<List<Images>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertImage(newImages: Images){
        coroutineScope.launch(Dispatchers.IO) {
            dao.insertImage(newImages)
        }
    }

    fun findImageId(id: Int) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindImageId(id).await()
        }
    }

    fun insertImageAsync(image: Images): Long {
        return dao.insertImageAwait(image)
    }

    fun findImagesOfPage(id: Int) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncfindImagesOfPage(id).await()
        }
    }

    fun asyncfindImageId(imageId: Int): Deferred<List<Images>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.findImage(imageId)
        }

    fun asyncfindImagesOfPage(pageId: Int): Deferred<List<Images>> =
        coroutineScope.async(Dispatchers.IO) {
            return@async dao.getAllImages(pageId)
        }
}