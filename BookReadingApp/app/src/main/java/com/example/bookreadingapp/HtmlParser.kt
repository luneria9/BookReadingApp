
import android.util.Log
import com.example.bookreadingapp.data.entities.Books
import com.example.bookreadingapp.data.entities.Chapters
import com.example.bookreadingapp.data.entities.Images
import com.example.bookreadingapp.data.entities.Pages
import com.example.bookreadingapp.data.entities.SubChapters
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.Scanner

class HtmlParser (viewModel: ReadingAppViewModel) {
    var html = StringBuilder()
    var string = ""
    private fun readFile(paths: MutableList<String>) :String {
        var data = ""
        for (path in paths){
            val myObj = File(path.substringAfter(":"))
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
    private var directory = ""

    private val unclosedTagList = mutableListOf<String>()

    // variables for adding to other database items
    private var bookStarted = false
    private var isChapter = false
    private var tableOpened = false
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
                Log.d("parsing", title)
                currentBook = result
            }
        }

        if (bookStarted) {
            when (unclosedTagList.lastOrNull()) {
                "h1", "h2", "h3", "h4", "h5", "h6" -> {
                    isChapter = true
                }
            }
            when (unclosedTagList.lastOrNull()) {
                "img" -> {
                    imageUrl = attributes["src"].toString()
                }
                "table" -> {
                    content += "<TABLE>"
                    tableOpened = true
                }
                "tbody" -> {
                    content +="<tbody>"
                }
                "tr" -> {
                    content += "<tr>"
                }
                "td" -> {
                    content += "<td>"
                }
                "th" -> {
                    content += "<th>"
                }
            }
        }
    }

    // Function to handle text and filter based on tags
    private fun handleText(text: String) {
        if (text.isBlank()) return

        when (unclosedTagList.lastOrNull()) {
            "style", "script", "link", "meta"-> return
            "i" -> {
                if (tableOpened) {
                    appendTextBasedOnTag(text)
                }
            }
            else -> appendTextBasedOnTag(text)
        }
    }

    // Function to append text based on specific HTML tags
    private fun appendTextBasedOnTag(text: String) {
        when (unclosedTagList.lastOrNull()) {
            "h1", "h2", "h3", "h4", "h5", "h6" -> {
                itemTitle += text
            }
            // if it's a chapter append to title
            else -> {
                if (isChapter) {
                    itemTitle += text
                }
                else {
                    content += text
                }
            }

        }
    }

    // Function to handle closing tags
    private fun handleCloseTag(viewModel: ReadingAppViewModel) {

        if (bookStarted) {
            if (unclosedTagList.lastOrNull() == "table") {
                content += "</TABLE>"
                tableOpened = false
            }
            when (unclosedTagList.lastOrNull()) {
                "tbody" -> {
                    content +="</tbody>"
                }
                "tr" -> {
                    content += "</tr>"
                }
                "td" -> {
                    content += "</td>"
                }
                "th" -> {
                    content += "</th>"
                }
            }
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
                    isChapter = false
                    itemTitle = ""
                }
            }
            when (unclosedTagList.lastOrNull()) {
                "h3", "h4", "h5", "h6" -> {
                    // check if there is no previous chapter if so add placeholder to database
                    if(currentChapter.title == "") {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnChapter(
                                Chapters("<PLACEHOLDER>", currentBook.id)
                            )
                        }
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

                    // reset chapter and title
                    isChapter = false
                    itemTitle = ""

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
                                Chapters("<PLACEHOLDER>", currentBook.id))
                        }
                        currentChapter = result
                    }
                    if(currentSubChapter.title == "") {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnSubChapter(
                                SubChapters("<PLACEHOLDER>", currentChapter.id)
                            )
                        }
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
                "img" -> {
                    // check if chapter and subchapter are empty if so add placeholders so database respect format
                    if(currentChapter.title == "") {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnChapter(
                                Chapters("<PLACEHOLDER>", currentBook.id))
                        }
                        currentChapter = result
                    }
                    if(currentSubChapter.title == "") {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnSubChapter(
                                SubChapters("<PLACEHOLDER>", currentChapter.id)
                            )
                        }
                        currentSubChapter = result
                    }
                    if(currentPage.pageNumber == 0) {
                        val result = runBlocking {
                            viewModel.asyncInsertAndReturnPages(
                                Pages(currentSubChapter.id, numOfPages, "<PLACEHOLDER>",)
                            )
                        }
                        content = ""
                        currentPage = result
                        numOfPages++
                    }
                    content += "<IMAGE>"
                    // pageid always one behind
                    viewModel.insertImage(Images(currentPage.id, "$directory/$imageUrl"))
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
        directory = listOfPaths[0].substringBeforeLast("/")
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
