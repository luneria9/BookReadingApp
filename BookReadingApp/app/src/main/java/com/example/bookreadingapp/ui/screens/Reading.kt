package com.example.bookreadingapp.ui.screens

import androidx.compose.foundation.horizontalScroll
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bookreadingapp.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookreadingapp.data.entities.Pages
import com.example.bookreadingapp.data.entities.SubChapters
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import com.mohamedrejeb.ksoup.entities.KsoupEntities
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlOptions
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

@Composable
fun ReadingScreen(
    preferences: SharedPreferences,
    readingMode: Boolean,
    onReadingCheck: (Boolean) -> Unit,
    bookId: Int,
    chapterId: Int,
    viewModel: ReadingAppViewModel,
    modifier: Modifier = Modifier
) {
    // Store the last book and chapter accessed
    with (preferences.edit()) {
        putInt(stringResource(R.string.last_location_book), bookId)
        putInt(stringResource(R.string.last_location_chapter), chapterId)
        apply()
    }

    // Observe content states
    val subChapters by remember(chapterId) {
        viewModel.findSubChaptersOfChapter(chapterId)
        viewModel.searchResultsSubChapters
    }.observeAsState(initial = emptyList())

    // Main container for the reading screen
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            // Render the content of the chapter
            ChapterContent(
                subChapters = subChapters,
                viewModel = viewModel,
                readingMode = readingMode
            )
        }
        // Display the reading mode toggle at the bottom of the screen
        ReadingMode(
            readingMode = readingMode,
            onReadingCheck = onReadingCheck,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// Composable to display the content of a chapter not working
@Composable
fun ChapterContent(
    subChapters: List<SubChapters>,
    viewModel: ReadingAppViewModel,
    readingMode: Boolean
) {
    subChapters.forEach { subChapter ->
        // Observe pages for each subchapter
        val pages by remember(subChapter.id) {
            viewModel.findPageOfSubChapter(subChapter.id)
            viewModel.searchResultsPages
        }.observeAsState(initial = emptyList())

        SubChapterSection(
            subChapter = subChapter,
            pages = pages,
            viewModel = viewModel,
            readingMode = readingMode
        )
    }
}

// Composable to display a subchapter and its pages not working
@Composable
fun SubChapterSection(
    subChapter: SubChapters,
    pages: List<Pages>,
    viewModel: ReadingAppViewModel,
    readingMode: Boolean
) {
    Text(
        text = subChapter.title,
        fontSize = dimensionResource(R.dimen.font_big).value.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium))
    )

    pages.forEach { page ->
        PageContent(
            page = page,
            viewModel = viewModel,
            readingMode = readingMode
        )
    }
}

// Composable to display the content of a page not working
@Composable
fun PageContent(
    page: Pages,
    viewModel: ReadingAppViewModel,
    readingMode: Boolean
) {
    // Observe images for the page
    val images by remember(page.id) {
        viewModel.findImagesOfPage(page.id)
        viewModel.searchResultsImages
    }.observeAsState(initial = emptyList())

    // Apply reading mode styles
    val textSize = if (readingMode) {
        dimensionResource(R.dimen.font_big).value.sp
    } else {
        dimensionResource(R.dimen.font_medium).value.sp
    }

    val tableHTML = getTableHTML(page.contents)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_small))
    ) {
        Text(
            text = page.contents,
            fontSize = textSize,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
        )

        images.forEach { image ->
            ImageContent(
                imageUrl = image.imageUrl
            )
        }

        Table(
            tableString = tableHTML,
            fontSize = textSize
        )
    }
}

/**
 * Finds table tags in HTML and returns the entire table element and its children.
 *
 * @param string HTML string.
 */
private fun getTableHTML(string: String): String {
    val startIndex = string.lowercase().indexOf("<table>")
    val endIndex = string.lowercase().indexOf("</table>") + "<table>".length

    return if (startIndex != -1 && endIndex != -1) {
        string.substring(startIndex, endIndex)
    } else { "" }
}

// Composable to display an image not working
@Composable
fun ImageContent(
    imageUrl: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_medium))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

// Composable to display the reading mode toggle
@Composable
fun ReadingMode(
    readingMode: Boolean,
    onReadingCheck: (Boolean) -> Unit,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.reading_mode),
            fontSize = dimensionResource(R.dimen.font_medium).value.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.spacer_medium)))
        Switch(
            checked = readingMode,
            onCheckedChange = onReadingCheck,
            modifier = Modifier.testTag("readingModeSwitch")
        )
    }
}

@Composable
fun Table(tableString: String, fontSize: TextUnit) {
    val doc: Document = Jsoup.parse(tableString)
    val rows = doc.select("tr")

    Column(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        rows.forEach {
            Row (
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.spacer_medium)
                )
            ) {
                val cells = it.select("td, th")
                cells.forEach { cell ->
                    Text(
                        text = cell.text(),
                        fontSize = fontSize,
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.spacer_small))
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TablePreview() {
    Table(
        tableString = "<table><tr><td>test</td><td>test2</td></tr><tr><td>test</td><td>test2</td><td>test3</td></tr><tr><td>test</td><td>test2</td></tr></table>",
        fontSize = dimensionResource(R.dimen.font_medium).value.sp
    )
}