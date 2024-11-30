package com.example.bookreadingapp.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookreadingapp.data.ReadingRoomDatabase
import com.example.bookreadingapp.data.entities.Books
import com.example.bookreadingapp.data.entities.Chapters
import com.example.bookreadingapp.data.entities.Images
import com.example.bookreadingapp.data.entities.Pages
import com.example.bookreadingapp.data.entities.SubChapters
import com.example.bookreadingapp.data.repository.BooksRepository
import com.example.bookreadingapp.data.repository.ChaptersRepository
import com.example.bookreadingapp.data.repository.ImageRepository
import com.example.bookreadingapp.data.repository.PageRepository
import com.example.bookreadingapp.data.repository.SubChaptersRepository
import com.example.bookreadingapp.fileSystem.FileSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReadingAppViewModel(private val fileSystem: FileSystem, application: Application) : ViewModel() {
    private val _directoryContents = MutableLiveData<List<String>>()
    val directoryContents: LiveData<List<String>> = _directoryContents

    var readingMode by mutableStateOf(false)

    fun toggleReadingMode() {
        readingMode = !readingMode
    }

    // from https://gitlab.com/crdavis/networkandfileio/-/tree/master?ref_type=heads
    // Function to set up file download
    fun setupDownload(url: String) {
            val fileName = url.substringAfterLast("/")
            val file = fileSystem.createFile("DownloadedFiles", fileName)

            if (fileSystem.downloadFile(url, file)) {
                updateDirectoryContents("DownloadedFiles")
            } else {
                Log.e("DownloadViewModel", "Failed to download file")
            }
    }

    fun unzipFile(fileName: String, destDirectory: String) {
            if (fileSystem.unzipFile(fileName, destDirectory, "DownloadedFiles")) {
                updateDirectoryContents("UnzippedBooks")
            } else {
                Log.e("DownloadViewModel", "Failed to unzip $fileName")
            }
    }

    fun downloadUnzip(url: String, fileName: String, destDirectory: String) {
        viewModelScope.launch(Dispatchers.Default){
            val downloadJob = launch(Dispatchers.IO) { setupDownload(url) }
            downloadJob.join()
            val unzipJob = launch(Dispatchers.Default) { unzipFile(fileName, destDirectory) }
        }
    }

    private fun updateDirectoryContents(directoryName: String) {
        val contents = fileSystem.listDirectoryContents(directoryName)
        _directoryContents.postValue(contents)
    }

    fun confirmDeletion(directoryName: String) {
        fileSystem.deleteDirectoryContents(directoryName)
        updateDirectoryContents(directoryName)
    }

    val allBooks: LiveData<List<Books>>
    private val booksRepository: BooksRepository
    private val chaptersRepository: ChaptersRepository
    private val subchaptersRepository: SubChaptersRepository
    private val pagesRepository: PageRepository
    val imagesRepository: ImageRepository
    val searchResultsBooks: MutableLiveData<List<Books>>
    val searchResultsChapters: MutableLiveData<List<Chapters>>
    val searchResultsSubChapters: MutableLiveData<List<SubChapters>>
    val searchResultsPages: MutableLiveData<List<Pages>>
    val searchResultsImages: MutableLiveData<List<Images>>

    init {
        val db = ReadingRoomDatabase.getInstance(application)
        val booksDao = db.booksDao()
        val chaptersDao = db.chaptersDao()
        val subchaptersDao = db.subchaptersDao()
        val pagesDao = db.pagesDao()
        val imagesDao = db.imagesDao()
        booksRepository = BooksRepository(booksDao)
        chaptersRepository = ChaptersRepository(chaptersDao)
        subchaptersRepository = SubChaptersRepository(subchaptersDao)
        pagesRepository = PageRepository(pagesDao)
        imagesRepository = ImageRepository(imagesDao)

        allBooks = booksRepository.allBooks
        searchResultsBooks = booksRepository.searchResults
        searchResultsChapters = chaptersRepository.searchResults
        searchResultsSubChapters = subchaptersRepository.searchResults
        searchResultsPages = pagesRepository.searchResults
        searchResultsImages = imagesRepository.searchResults
    }

    //books
    fun insertBook(book: Books){
        booksRepository.insertBook(book)
    }

    fun findBookId(id: Int){
        booksRepository.findBookId(id)
    }

    fun findBookTitle(title: String) {
        booksRepository.findBookTitle(title)
    }

    // chapters
    fun insertChapter(chapters: Chapters){
        chaptersRepository.insertChapter(chapters)
    }

    fun findChapterId(id: Int){
        chaptersRepository.findChapterId(id)
    }

    fun findChapterTitle(title: String){
        chaptersRepository.findChapterTitle(title)
    }

    fun findChaptersFromBook(id: Int){
        chaptersRepository.findChaptersOfBook(id)
    }

    // subchapters
    fun insertSubChapter(subChapters: SubChapters){
        subchaptersRepository.insertSubChapter(subChapters)
    }

    fun findSubChapterId(id: Int){
        subchaptersRepository.findSubChapterId(id)
    }

    fun findSubChapterTitle(title: String){
        subchaptersRepository.findSubChapterTitle(title)
    }

    fun findSubChaptersOfChapter(id: Int){
        subchaptersRepository.findSubChaptersOfChapter(id)
    }

    // pages
    fun insertPage(pages: Pages){
        pagesRepository.insertPage(pages)
    }

    fun findPageId(id: Int){
        pagesRepository.findPageId(id)
    }

    fun findPageNumber(pageNumber: Int){
        pagesRepository.findPageNumber(pageNumber)
    }

    fun findPageOfSubChapter(id: Int){
        pagesRepository.findPagesOfSubchapter(id)
    }

    // images
    fun insertImage(images: Images){
        imagesRepository.insertImage(images)
    }

    fun findImageId(id: Int){
        imagesRepository.findImageId(id)
    }

    fun findImagesOfPage(id: Int){
        imagesRepository.findImagesOfPage(id)
    }

//     for testing inserting
    fun testAll() {
        viewModelScope.launch(Dispatchers.Default){
            val insertBook = launch(Dispatchers.IO){ insertBook(Books("test title", "test author", "test subject", "test date"))}
            insertBook.join()
            Log.d("insert", "book")
            val insertChapter = launch(Dispatchers.IO) {insertChapter(Chapters("chapter1", 1))}
            insertChapter.join()
            Log.d("insert", "chapter")
            val insertSubChapter = launch(Dispatchers.IO) {insertSubChapter(SubChapters("test subchapter", 1))}
            insertSubChapter.join()
            Log.d("insert", "subchapter")
            val insertPage = launch(Dispatchers.IO){insertPage(Pages(1, 1, "test content"))}
            insertPage.join()
            Log.d("insert", "page")
            val insertImage = launch(Dispatchers.IO){insertImage(Images(1, "url"))}
            Log.d("insert", "image")
        }
    }

    fun testFindId() {
        booksRepository.findBookId(1)
        chaptersRepository.findChapterId(1)
        subchaptersRepository.findSubChapterId(1)
        pagesRepository.findPageId(1)
        imagesRepository.findImageId(1)
    }

    fun testFindAllOf() {
        chaptersRepository.findChaptersOfBook(1)
        subchaptersRepository.findSubChaptersOfChapter(1)
        pagesRepository.findPagesOfSubchapter(1)
        imagesRepository.findImagesOfPage(1)
    }


}