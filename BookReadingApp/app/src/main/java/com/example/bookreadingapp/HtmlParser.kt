import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import org.jsoup.Jsoup
import java.io.File


var html = StringBuilder();


fun readFile(path: String) :String {
    val doc = Jsoup.parse(File(path), "UTF-8")

    return doc.body().text();
}

// String to store the extracted text
var string = ""
val unclosedTagList = mutableListOf<String>()

// Create a handler
// TODO fix table handling
val handler = KsoupHtmlHandler
    .Builder()
    .onOpenTag { name, _, _ ->
        unclosedTagList.add(name)
    }
    .onText { text ->
        if (text.isBlank()) return@onText
        else if(unclosedTagList.lastOrNull() == "style") return@onText
        else if (unclosedTagList.lastOrNull() == "script") return@onText
        else if (unclosedTagList.lastOrNull() == "link") return@onText
        else if (unclosedTagList.lastOrNull() == "meta") return@onText
        else if (unclosedTagList.lastOrNull() == "image") string += """
            IMAGE PLACEHOLDER
            
        """.trimIndent()
        else if (unclosedTagList.lastOrNull() == "table") string += """
            TABLE PLACEHOLDER
        """.trimIndent()
        else if (unclosedTagList.lastOrNull() == "th") string +=""
        else if (unclosedTagList.lastOrNull() == "tr") string +=""
        else if (unclosedTagList.lastOrNull() == "tbody") string +=""
        else string += """
            $text
        """.trimIndent()
    }
    .onCloseTag { name, _ ->
        unclosedTagList.removeLastOrNull();
    }
    .build()

fun main() {
    // REPLACE WITH PATH
    var htmlstring = readFile("")
    // Create a parser
    val ksoupHtmlParser = KsoupHtmlParser(
        handler = handler,
    )

    // Pass the HTML to the parser (It is going to parse the HTML and call the callbacks)
    ksoupHtmlParser.write(htmlstring)

    // Close the parser when you are done
    ksoupHtmlParser.end()

    println(string)
}

