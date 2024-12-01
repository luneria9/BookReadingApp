package com.example.bookreadingapp.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.bookreadingapp.R
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.bookreadingapp.data.entities.Pages
import com.example.bookreadingapp.data.entities.SubChapters
import com.example.bookreadingapp.viewModels.ReadingAppViewModel

@Composable
fun ReadingScreen(
    bookId: Int,
    chapterId: Int,
    viewModel: ReadingAppViewModel,
    modifier: Modifier = Modifier
) {
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
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            ChapterContent(
                subChapters = subChapters,
                viewModel = viewModel,
                readingMode = readingMode
            )

            ReadingMode(
                readingMode = readingMode,
                onReadingCheck = { viewModel.toggleReadingMode() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
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