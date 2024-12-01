package com.example.bookreadingapp.viewModels

import HtmlParser
import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume

class ReadingAppViewModel(private val fileSystem: FileSystem, application: Application) : ViewModel() {
    private val _directoryContents = MutableLiveData<List<String>>()
    private val applicationContext = application
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

    suspend fun downloadUnzip(url: String, fileName: String, destDirectory: String) {
        viewModelScope.launch(Dispatchers.Default) {
            // Download file
            val downloadJob = launch(Dispatchers.IO) { setupDownload(url) }
            downloadJob.join()

            // Unzip file
            val unzipJob = launch(Dispatchers.Default) { unzipFile(fileName, destDirectory) }
            unzipJob.join()

            // Get the path of the unzipped HTML file
            val unzippedPath = "${applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/$destDirectory"

            // Parse and insert the book data
            parseAndInsert(mutableListOf("$unzippedPath/index.html"))
        }
    }

    fun getCoverImagePath(bookDirectory: String): String {
        val dir = File(bookDirectory)
        return dir.walkTopDown()
            .find { file ->
                file.name.endsWith("-cover.png", ignoreCase = true) || file.name.endsWith("-cover.jpg", ignoreCase = true)
            }
            ?.absolutePath ?: ""
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


    suspend fun asyncInsertAndReturnBook(book: Books): Books {
        val insertGetBook = runBlocking {
            withContext(Dispatchers.IO) {
                val insert = booksRepository.insertBookAsync(book)
                val getBook = async(Dispatchers.IO) {
                    val result = booksRepository.asyncfindBookId(insert.toInt())
                    return@async result.await()[0]
                }
                return@withContext getBook.await()
            }
        }
        return insertGetBook
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

    suspend fun asyncInsertAndReturnChapter(chapter: Chapters): Chapters {
        val insertGet = runBlocking {
            withContext(Dispatchers.IO) {
                val insert = chaptersRepository.insertChapterAsync(chapter)
                val get = async(Dispatchers.IO) {
                    val result = chaptersRepository.asyncfindChapterId(insert.toInt())
                    return@async result.await()[0]
                }
                return@withContext get.await()
            }
        }
        return insertGet
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

    suspend fun asyncInsertAndReturnSubChapter(subChapter: SubChapters): SubChapters {
        val insertGet = runBlocking {
            withContext(Dispatchers.IO) {
                val insert = subchaptersRepository.insertSubChapterAsync(subChapter)
                val get = async(Dispatchers.IO) {
                    val result = subchaptersRepository.asyncfindSubChapterId(insert.toInt())
                    return@async result.await()[0]
                }
                return@withContext get.await()
            }
        }
        return insertGet
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

    suspend fun asyncInsertAndReturnPages(page: Pages): Pages {
        val insertGet = runBlocking {
            withContext(Dispatchers.IO) {
                val insert = pagesRepository.insertPageAsync(page)
                delay(page.contents.length.toLong())
                Log.d("length" , page.contents.length.toString())
                val get = async(Dispatchers.IO) {
                    val result = pagesRepository.asyncfindPageId(insert.toInt())
                    return@async result.await()[0]
                }
                return@withContext get.await()
            }
        }
        return insertGet
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

    fun parseAndInsert(listOfPaths: MutableList<String>){
        val directory = this.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+"/Physics/pg40175-images.html";
        val viewModel = this
        viewModelScope.launch(Dispatchers.Default) {
            val parser = HtmlParser(viewModel = viewModel);
            parser.parse(mutableListOf(directory), viewModel);
        }

    }
}