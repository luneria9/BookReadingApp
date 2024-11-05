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

// Create a handler
val handler = KsoupHtmlHandler
    .Builder()
    .onOpenTag { name, _, _ ->
        unclosedTagList.add(name)
        if (unclosedTagList.lastOrNull() == "img") string += """
            IMAGE PLACEHOLDER
        """
        if (unclosedTagList.lastOrNull() == "table") string += """
            TABLE PLACEHOLDER
        """
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
        else if (unclosedTagList.lastOrNull() == "h1") string += """
            
            $text
            
        """
        else if (unclosedTagList.lastOrNull() == "h1") string += """
            
            $text
            
        """
        else if (unclosedTagList.lastOrNull() == "h2") string += """
            
            $text
            
        """
        else if (unclosedTagList.lastOrNull() == "h3") string += """
            
            $text
            
        """
        else if (unclosedTagList.lastOrNull() == "h4") string += """
            
            $text
            
        """
        else if (unclosedTagList.lastOrNull() == "h5") string += """
            
            $text
            
        """
        else if (unclosedTagList.lastOrNull() == "h6") string += """
            
            $text
            
            
        """
        else string += """
            $text
        """.trimIndent()
    }
    .onCloseTag { _, _ ->
        if (unclosedTagList.lastOrNull() == "section") string += """
            
        """
        unclosedTagList.removeLastOrNull()

    }
    .build()

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
        handler = handler,
    )

    ksoupHtmlParser.write(htmlstring)

    // Close the parser when you are done
    ksoupHtmlParser.end()

    println(string)
}

