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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookreadingapp.data.entities.Pages
import com.example.bookreadingapp.data.entities.SubChapters
import com.example.bookreadingapp.viewModels.ReadingAppViewModel

@Composable
fun ReadingScreen(
    preferences: SharedPreferences,
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

    // Reading mode state
    val readingMode by remember { mutableStateOf(viewModel.readingMode) }
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            ChapterContent(
                subChapters = subChapters,
                viewModel = viewModel,
                readingMode = readingMode
            )
        }
        ReadingMode(
            readingMode = readingMode,
            onReadingCheck = onReadingCheck,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

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
                imageUrl = image.imageUrl,
                readingMode = readingMode
            )
        }
    }
}

@Composable
fun ImageContent(
    imageUrl: String,
    readingMode: Boolean
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

//@Preview(showBackground = true)
//@Composable
//fun ReadingScreenPreview() {
//    BookReadingAppTheme {
//        ReadingScreen(false, {})
//    }
//}
//
//@Composable
//@Preview(showBackground = true, locale = "fr")
//fun ReadingScreenPreviewFr() {
//    BookReadingAppTheme {
//        val navController = rememberNavController()
//        ReadingScreen(false, {})
//    }
//}