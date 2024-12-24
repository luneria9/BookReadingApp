package com.example.bookreadingapp.viewModels

import HtmlParser
import android.app.Application
import android.os.Environment
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
import com.example.bookreadingapp.data.fileSystem.FileSystem
import com.example.bookreadingapp.data.repository.BooksRepository
import com.example.bookreadingapp.data.repository.ChaptersRepository
import com.example.bookreadingapp.data.repository.ImageRepository
import com.example.bookreadingapp.data.repository.PageRepository
import com.example.bookreadingapp.data.repository.SubChaptersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class ReadingAppViewModel(private val fileSystem: FileSystem, application: Application) : ViewModel() {
    private val _directoryContents = MutableLiveData<List<String>>()
    private val applicationContext = application
    var readingMode by mutableStateOf(false)
    val expandedChapters = MutableLiveData<Set<Int>>(emptySet())
    var selectedBookId: Int? = null
    val downloadedTitles = MutableLiveData<MutableList<String>>()

    fun toggleReadingMode() {
        readingMode = !readingMode
    }

    fun performSearch(query: String) {
        if (selectedBookId != null) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val chapters = chaptersRepository.searchChapters(query, selectedBookId!!)
                    val subChapters = subchaptersRepository.searchSubChapters(query, selectedBookId!!)
                    val pages = pagesRepository.searchPages(query, selectedBookId!!)

                    // Post results to LiveData
                    searchResultsChapters.postValue(chapters)
                    searchResultsSubChapters.postValue(subChapters)
                    searchResultsPages.postValue(pages)
                }
            }
        }
    }

    fun addDownload(title: String) {
        val updatedList = downloadedTitles.value.orEmpty().toMutableList().apply { add(title) }
        downloadedTitles.value = updatedList
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
            Log.d("job", "downloading")
            val downloadJob = launch(Dispatchers.IO) { setupDownload(url) }
            downloadJob.join()
            Log.d("job", "unzip")

            // Unzip file
            val unzipJob = launch(Dispatchers.IO) { unzipFile(fileName, destDirectory) }
            unzipJob.join()

            // Get the path of the unzipped HTML file
            val unzippedPath = "${applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/$destDirectory"

            val htmlFiles = File(unzippedPath).walk()
                .filter { it.isFile && it.extension.equals("html", ignoreCase = true) }
                .map { it.toURI().toString() }
                .toList()
            // Parse and insert the book data
            Log.d("job", "parsing")
            launch(Dispatchers.IO) { parseAndInsert(htmlFiles.toMutableList()) }
        }
    }

    // Returns the path of the cover image for a given book directory
    fun getCoverImagePath(bookDirectory: String): String {
        val dir = File(bookDirectory)
        return dir.walkTopDown()
            .find { file ->
                file.name.endsWith("-cover.png", ignoreCase = true) || file.name.endsWith("-cover.jpg", ignoreCase = true)
            }
            // If no cover image is found, it returns an empty string.
            ?.absolutePath ?: ""
    }

    private fun updateDirectoryContents(directoryName: String) {
        val contents = fileSystem.listDirectoryContents(directoryName)
        _directoryContents.postValue(contents)
    }

    private val pagesMap = mutableMapOf<Int, MutableLiveData<List<Pages>>>()

    fun getSetPagesOfSubChapter(subChapterId: Int): LiveData<List<Pages>> {
        return pagesMap.getOrPut(subChapterId) {
            MutableLiveData<List<Pages>>().also { liveData ->
                // Fetch pages for the subchapter and post the result
                viewModelScope.launch {
                    val pages = asyncFindPageOfSubChapter(subChapterId)
                    liveData.postValue(pages)
                }
            }
        }
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

    fun toggleChapterExpansion(chapterId: Int) {
        val currentSet = expandedChapters.value ?: emptySet()
        expandedChapters.value = if (currentSet.contains(chapterId)) {
            currentSet - chapterId
        } else {
            currentSet + chapterId
        }
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

    suspend fun asyncFindPageOfSubChapter(id: Int): List<Pages> {
        val result = pagesRepository.asyncfindPagesOfSubchapter(id)
        return result.await()
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

    private fun parseAndInsert(listOfPaths: MutableList<String>){
        val viewModel = this
        viewModelScope.launch(Dispatchers.Default) {
            val parser = HtmlParser(viewModel = viewModel)
            parser.parse(listOfPaths, viewModel);
        }

    }
}