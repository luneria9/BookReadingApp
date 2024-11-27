import android.os.Build
import androidx.annotation.RequiresApi
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import java.io.File
import java.nio.file.Paths
import java.util.Scanner


var html = StringBuilder()
fun readFile(paths: MutableList<String>) :String {
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
var string = ""
val unclosedTagList = mutableListOf<String>()

// Function to handle open tags and add placeholders
fun handleOpenTag(name: String) {
    unclosedTagList.add(name)
    when (unclosedTagList.lastOrNull()) {
        "img" -> string += "\nIMAGE PLACEHOLDER"
        "table" -> string += "\nTABLE PLACEHOLDER"
    }
}

// Function to handle text and filter based on tags
fun handleText(text: String) {
    if (text.isBlank()) return

    when (unclosedTagList.lastOrNull()) {
        "style", "script", "link", "meta", "th", "tr", "td", "tbody" -> return
        else -> appendTextBasedOnTag(text)
    }
}

// Function to append text based on specific HTML tags
fun appendTextBasedOnTag(text: String) {
    when (unclosedTagList.lastOrNull()) {
        "h1", "h2", "h3", "h4", "h5", "h6" -> string += "\n$text\n"
        else -> string += "\n$text"
    }
}

// Function to handle closing tags
fun handleCloseTag() {
    if (unclosedTagList.lastOrNull() == "section") {
        string += "\n"
    }
    unclosedTagList.removeLastOrNull()
}

// Function to initialize and return the KsoupHtmlHandler
fun createKsoupHandler(): KsoupHtmlHandler {
    return KsoupHtmlHandler
        .Builder()
        .onOpenTag { name, _, _ -> handleOpenTag(name) }
        .onText { text -> handleText(text) }
        .onCloseTag { _, _ -> handleCloseTag() }
        .build()
}

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    // REPLACE WITH PATH
    // does not replace <i>
    val path = Paths.get("./app/src/main/assets/book1.html").toAbsolutePath().toString()
    val path2 = Paths.get("./app/src/main/assets/book2.html").toAbsolutePath().toString()
    val list : MutableList<String> = mutableListOf(path, path2)
    val htmlstring = readFile(list)
    // Create a parser
    val ksoupHtmlParser = KsoupHtmlParser(
        handler = createKsoupHandler(),
    )

    ksoupHtmlParser.write(htmlstring)

    // Close the parser when you are done
    ksoupHtmlParser.end()

    println(string)
}