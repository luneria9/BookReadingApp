import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import org.jsoup.Jsoup
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files


var html = StringBuilder();


fun readFile(path: String) :String {
    var data =""
    var myObj = File(path);
    var myReader = Scanner(myObj);
    while (myReader.hasNextLine()) {
        data += myReader.nextLine();
    }
    myReader.close();
    return data
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
        if (unclosedTagList.lastOrNull() == "img") string += """
            IMAGE PLACEHOLDER
        """.trimIndent()
        if (unclosedTagList.lastOrNull() == "table") string += """
            TABLE PLACEHOLDER
        """.trimIndent()
    }
    .onText { text ->
        if (text.isBlank()) return@onText
        else if(unclosedTagList.lastOrNull() == "style") return@onText
        else if (unclosedTagList.lastOrNull() == "script") return@onText
        else if (unclosedTagList.lastOrNull() == "link") return@onText
        else if (unclosedTagList.lastOrNull() == "meta") return@onText
        else if (unclosedTagList.lastOrNull() == "th") return@onText
        else if (unclosedTagList.lastOrNull() == "tr") return@onText
        else if (unclosedTagList.lastOrNull() == "td") return@onText
        else if (unclosedTagList.lastOrNull() == "tbody") return@onText
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
    // does not replace <i>
    var htmlstring = readFile("")
    // Create a parser
    val ksoupHtmlParser = KsoupHtmlParser(
        handler = handler,
    )

    // Pass the HTML to the parser (It is going to parse the HTML and call the callbacks)
//    var testString = """
//    <html>
//        <head>
//            <title>My Title</title>
//        </head>
//        <body>
//            <h1>My Heading</h1>
//            <p>My paragraph.</p>
//        </body>
//        <table>
//          <tr>
//            <th>Company</th>
//            <th>Contact</th>
//            <th>Country</th>
//          </tr>
//        </table>
//    </html>
//""".trimIndent()

    ksoupHtmlParser.write(htmlstring)

    // Close the parser when you are done
    ksoupHtmlParser.end()

    println(string)
}

