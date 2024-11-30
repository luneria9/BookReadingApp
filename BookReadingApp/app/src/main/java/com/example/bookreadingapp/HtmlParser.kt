import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.bookreadingapp.data.entities.Books
import com.example.bookreadingapp.data.entities.Chapters
import com.example.bookreadingapp.data.entities.Images
import com.example.bookreadingapp.data.entities.Pages
import com.example.bookreadingapp.data.entities.SubChapters
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import com.example.bookreadingapp.viewModels.ReadingAppViewModelFactory
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Paths
import java.util.Scanner

class HtmlParser (viewModel: ReadingAppViewModel) {
    var html = StringBuilder()
    var string = ""
    private fun readFile(paths: MutableList<String>) :String {
        var data = ""
        for (path in paths){
            val myObj = File(path)
            val myReader = Scanner(myObj)
            while (myReader.hasNextLine()) {
                data += myReader.nextLine()
            }
            myReader.close()
        }
        return data
    }

    // String to store the extracted text

    // variables for creating book object
    private var title = ""
    private val authors = mutableListOf<String>()
    private var subject = ""
    private var release = ""

    private val unclosedTagList = mutableListOf<String>()

    // variables for adding to other database items
    private var bookStarted = false
    private var content = ""
    private var itemTitle = ""

    // keep track of the parent component
    private var currentBook = Books()
    private var currentChapter = Chapters()
    private var currentSubChapter = SubChapters()
    private var currentPage = Pages()
    private var imageUrl = ""
    private var numOfPages = 1
    private var identifier = 0


    // Function to handle open tags and add placeholders
    private fun handleOpenTag(name: String, attributes: Map<String, String>, viewModel: ReadingAppViewModel) {
        unclosedTagList.add(name)
        // getting the title, authors, data and subject
        if (unclosedTagList.lastOrNull() == "meta"){
            if(attributes["name"] == "dc.title"){
                title = attributes["content"].toString()
            }
            else if (attributes["name"] == "dc.creator"){
                authors.add(attributes["content"].toString())
            }
            else if (attributes["name"] == "dcterms.created"){
                release = attributes["content"].toString()
            }
            else if (attributes["name"] == "dc.subject"){
                subject = attributes["content"].toString()
            }
        }

        // not inserting non relevant information
        if (bookStarted == false){
            if (attributes["id"] == "pg-start-separator"){
                bookStarted = true

                // gets all the author names puts in a string with commas seperating except last
                var authorsString = ""
                for (author in authors) {
                    authorsString += "$author,"
                }
                authorsString.dropLast(1)


                // add job to insert and then get retrieve the book
                val result = runBlocking {
                    viewModel.asyncInsertAndReturnBook(Books(title, authorsString, subject, release))
                }
                currentBook = result
            }
        }

        when (unclosedTagList.lastOrNull()) {
            "img" -> {
                string += "\nIMAGE PLACEHOLDER"
                imageUrl = attributes["src"].toString()
            }
            "table" -> {
                string += "\n<TABLE>"
                content += "<TABLE>"
            }
        }
    }

    // Function to handle text and filter based on tags
    private fun handleText(text: String) {
        if (text.isBlank()) return

        when (unclosedTagList.lastOrNull()) {
            "style", "script", "link", "meta", "i"-> return
            else -> appendTextBasedOnTag(text)
        }
    }

    // Function to append text based on specific HTML tags
    private fun appendTextBasedOnTag(text: String) {
        when (unclosedTagList.lastOrNull()) {
            "h1", "h2", "h3", "h4", "h5", "h6" -> {
                string += "\n$text\n"
                itemTitle = text
            }
            else -> {
                string += "\n$text"
                content += text
            }

        }
    }

    // Function to handle closing tags
    private fun handleCloseTag(viewModel: ReadingAppViewModel) {

        if (bookStarted) {
            if (unclosedTagList.lastOrNull() == "section") {
                string += "\n"
            }
            if (unclosedTagList.lastOrNull() == "section") {
                string += "</TABLE>"
                content += "</TABLE>"
            }
            // add item to database depending on what is closing
            // close on image or header or section or table

            when (unclosedTagList.lastOrNull()) {
                "h1", "h2" -> {
                    // add chapter
                    val result = runBlocking {
                        viewModel.asyncInsertAndReturnChapter(Chapters(itemTitle, currentBook.id)
                        )
                    }
                    // set reference to chapter so subchapter can use it
                    currentChapter = result

                    // resets subchapter and pages so we don't write to another chapter
                    currentSubChapter = SubChapters()
                    currentPage = Pages()
                }
            }
            when (unclosedTagList.lastOrNull()) {
                "h3", "h4", "h5", "h6" -> {
                    // check if there is no previous chapter if so add placeholder to database
                    if(currentChapter.title == "") {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnChapter(
                                Chapters("PLACEHOLDER CHAPTER$identifier", currentBook.id)
                            )
                        }
                        identifier++
                        currentChapter = result
                    }
                    // add subchapter
                    val result = runBlocking {
                        viewModel.asyncInsertAndReturnSubChapter(
                            SubChapters(itemTitle, currentChapter.id)
                        )
                    }
                    // add reference to subchapter for pages
                    currentSubChapter = result

                    // reset pages so we don't write to another subchapter
                    currentPage = Pages()
                }
            }
            when (unclosedTagList.lastOrNull()) {
                "section", "div", "p" -> {
                    // check if chapter and subchapter are empty if so add placeholders so database respect format
                    if(currentChapter.title == "") {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnChapter(
                                Chapters("PLACEHOLDER CHAPTER$identifier", currentBook.id))
                        }
                        identifier++
                        currentChapter = result
                    }
                    if(currentSubChapter.title == "") {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnSubChapter(
                                SubChapters("PLACEHOLDER SUBCHAPTER$identifier", currentChapter.id)
                            )
                        }
                        identifier++
                        currentSubChapter = result
                    }
                    // add the page
                    val result = runBlocking {
                        viewModel.asyncInsertAndReturnPages(
                            Pages(currentSubChapter.id, numOfPages, content)
                        )
                    }

                    // set reference so image can use id
                    currentPage = result
                    content = ""
                    numOfPages++
                }
            }
            when (unclosedTagList.lastOrNull()) {
                // images is always going to end up at the end of a page
                "image" -> {
                    // check if chapter and subchapter are empty if so add placeholders so database respect format
                    if(currentChapter.title == "") {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnChapter(
                                Chapters("PLACEHOLDER CHAPTER$identifier", currentBook.id))
                        }
                        identifier++
                        currentChapter = result
                    }
                    if(currentSubChapter.title == "") {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnSubChapter(
                                SubChapters("PLACEHOLDER SUBCHAPTER$identifier", currentChapter.id)
                            )
                        }
                        identifier++
                        currentSubChapter = result
                    }
                    if(currentPage.pageNumber == 0) {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnPages(
                                Pages(currentSubChapter.id, numOfPages, "PLACEHOLDER$identifier",)
                            )
                        }
                        identifier++
                        content = ""
                        currentPage = result
                        numOfPages++
                    }
                    viewModel.insertImage(Images(currentPage.id, imageUrl))
                }
            }
        }
        unclosedTagList.removeLastOrNull()


    }

    // Function to initialize and return the KsoupHtmlHandler
    private fun createKsoupHandler(viewModel: ReadingAppViewModel): KsoupHtmlHandler {
        return KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attributes, _ -> handleOpenTag(name, attributes, viewModel = viewModel) }
            .onText { text -> handleText(text) }
            .onCloseTag { _, _ -> handleCloseTag(viewModel = viewModel) }
            .build()
    }

    fun parse(listOfPaths: MutableList<String>, viewModel: ReadingAppViewModel): String {
        val htmlString = readFile(listOfPaths)
        string = ""
        unclosedTagList.clear()
        // Create a parser
        val ksoupHtmlParser = KsoupHtmlParser(
            handler = createKsoupHandler(viewModel),
        )

        ksoupHtmlParser.write(htmlString)

        // Close the parser when you are done
        ksoupHtmlParser.end()
        return string
    }
}

//fun main() {
//    // REPLACE WITH PATH
//    // does not replace <i>
//    val path = Paths.get("./app/src/main/assets/book1.html").toAbsolutePath().toString()
//    val path2 = Paths.get("./app/src/main/assets/book2.html").toAbsolutePath().toString()
//    val list : MutableList<String> = mutableListOf(path, path2)
//    val parser = HtmlParser(ReadingAppViewModelFactory)
//
//    var string = parser.parse(list)
//
//    println(string)
//}