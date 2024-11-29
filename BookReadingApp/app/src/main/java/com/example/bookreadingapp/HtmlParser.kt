import android.os.Build
import androidx.annotation.RequiresApi
import com.example.bookreadingapp.data.entities.Books
import com.example.bookreadingapp.data.entities.Chapters
import com.example.bookreadingapp.data.entities.Pages
import com.example.bookreadingapp.data.entities.SubChapters
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import java.io.File
import java.nio.file.Paths
import java.util.Scanner

class HtmlParser () {
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

    // Function to handle open tags and add placeholders
    private fun handleOpenTag(name: String, attributes: Map<String, String>) {
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
            }
        }

        when (unclosedTagList.lastOrNull()) {
            "img" -> string += {
                imageUrl = attributes["src"].toString()
                "\nIMAGE PLACEHOLDER"
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
    private fun handleCloseTag() {
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
            "h1", "h2", "h3" -> {
                // TODO add Chapter and while loop until job is finished
            }
        }
        when (unclosedTagList.lastOrNull()) {
            "h4", "h5", "h6" -> {
                // TODO add SubChapter
            }
        }
        when (unclosedTagList.lastOrNull()) {
            "section", "div", "p" -> {
                // TODO add Pages
            }
        }
        when (unclosedTagList.lastOrNull()) {
            "image" -> {
                // TODO add Image ref to page
            }
        }

        unclosedTagList.removeLastOrNull()


    }

    // Function to initialize and return the KsoupHtmlHandler
    private fun createKsoupHandler(): KsoupHtmlHandler {
        return KsoupHtmlHandler
            .Builder()
            .onOpenTag { name, attributes, _ -> handleOpenTag(name, attributes) }
            .onText { text -> handleText(text) }
            .onCloseTag { _, _ -> handleCloseTag() }
            .build()
    }

    fun parse(listOfPaths: MutableList<String>): String {
//        val htmlstring = readFile(listOfPaths)
        val htmlstring = "" +
                "<meta name=\"dc.title\" content=\"Physics\">\n" +
                "<meta name=\"dc.language\" content=\"en\">\n" +
                "<meta name=\"dcterms.source\" content=\"https://www.gutenberg.org/files/40175/40175-h/40175-h.htm\">\n" +
                "<meta name=\"dcterms.modified\" content=\"2024-11-17T09:41:45.035897+00:00\">\n" +
                "<meta name=\"dc.rights\" content=\"Public domain in the USA.\">\n" +
                "<link rel=\"dcterms.isFormatOf\" href=\"http://www.gutenberg.org/ebooks/40175\">\n" +
                "<meta name=\"dc.creator\" content=\"Tower, Willis E. (Willis Eugene), 1871-\">\n" +
                "<meta name=\"dc.creator\" content=\"Cope, Thomas D. (Thomas Darlington), 1880-1964\">\n" +
                "<meta name=\"dc.creator\" content=\"Smith, Charles H. (Charles Henry), 1861-1926\">\n" +
                "<meta name=\"dc.creator\" content=\"Turton, Charles M. (Charles Mark), 1861-1937\">\n" +
                "<meta name=\"dc.subject\" content=\"Physics\">\n" +
                "<meta name=\"dcterms.created\" content=\"2012-07-09\">\n" +
                "<meta name=\"generator\" content=\"Ebookmaker 0.12.47 by Project Gutenberg\">\n" +
                "<meta property=\"og:title\" content=\"Physics\">\n" +
                "<meta property=\"og:type\" content=\"Text\">\n" +
                "<meta property=\"og:url\" content=\"https://www.gutenberg.org/cache/epub/40175/pg40175-images.html\">\n" +
                "<meta property=\"og:image\" content=\"https://www.gutenberg.org/cache/epub/40175/pg40175.cover.medium.jpg\">"
        string = ""
        unclosedTagList.clear()
        // Create a parser
        val ksoupHtmlParser = KsoupHtmlParser(
            handler = createKsoupHandler(),
        )

        ksoupHtmlParser.write(htmlstring)

        // Close the parser when you are done
        ksoupHtmlParser.end()

        println(title)
        println(subject)
        println(release)
        println(authors)
        return string
    }
}

fun main() {
    // REPLACE WITH PATH
    // does not replace <i>
    val path = Paths.get("./app/src/main/assets/book1.html").toAbsolutePath().toString()
    val path2 = Paths.get("./app/src/main/assets/book2.html").toAbsolutePath().toString()
    val list : MutableList<String> = mutableListOf(path, path2)
    val parser = HtmlParser()

    var string = parser.parse(list)

    println(string)
}