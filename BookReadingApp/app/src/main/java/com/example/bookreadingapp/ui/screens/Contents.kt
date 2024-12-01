package com.example.bookreadingapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.bookreadingapp.R
import com.example.bookreadingapp.data.entities.Chapters
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.viewModels.ReadingAppViewModel

@Composable
fun ContentsScreen(
    bookId: Int,
    viewModel: ReadingAppViewModel
) {
    // Observe chapters
    val chapters by remember(bookId) {
        viewModel.findChaptersFromBook(bookId)
        viewModel.searchResultsChapters
    }.observeAsState(initial = emptyList())

    // State to track expanded chapters
    val expandedChapters = remember { mutableStateOf(emptySet<Int>()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TitleText()
        ChapterList(
            chapters = chapters,
            expandedChapters = expandedChapters.value,
            onChapterClick = { chapterId ->
                viewModel.toggleChapterExpansion(chapterId)
            },
            viewModel = viewModel
        )
    }
}

@Composable
fun ChapterList(
    chapters: List<Chapters>,
    expandedChapters: Set<Int>,
    onChapterClick: (Int) -> Unit,
    viewModel: ReadingAppViewModel
) {
    chapters.forEach { chapter ->
        val isExpanded = expandedChapters.contains(chapter.id)
        ChapterWithSubChapters(
            chapter = chapter,
            isExpanded = isExpanded,
            onChapterClick = onChapterClick,
            viewModel = viewModel
        )
    }
}

@Composable
fun ChapterWithSubChapters(
    chapter: Chapters,
    isExpanded: Boolean,
    onChapterClick: (Int) -> Unit,
    viewModel: ReadingAppViewModel
) {
    ChapterRow(
        chapter = chapter.title,
        page = chapter.id.toString(),
        onClick = { onChapterClick(chapter.id) }
    )

    if (isExpanded) {
        SubChapterList(chapterId = chapter.id, viewModel = viewModel)
    }
}

@Composable
fun SubChapterList(
    chapterId: Int,
    viewModel: ReadingAppViewModel
) {
    val subChapters by remember(chapterId) {
        viewModel.findSubChaptersOfChapter(chapterId)
        viewModel.searchResultsSubChapters
    }.observeAsState(initial = emptyList())

    subChapters.forEach { subChapter ->
        SubChapterRow(
            subchapter = subChapter.title,
            page = subChapter.id.toString()
        )
    }
}

@Composable
fun TitleText() {
    Text(
        text = stringResource(R.string.contents),
        fontSize = dimensionResource(R.dimen.font_big).value.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
    )
}

@Composable
fun ChapterRow(
    chapter: String,
    page: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = chapter,
            textAlign = TextAlign.Left,
            fontSize = dimensionResource(R.dimen.font_medium).value.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        )

        Text(
            text = page,
            textAlign = TextAlign.Right,
            fontWeight = FontWeight.Medium,
            fontSize = dimensionResource(R.dimen.font_medium).value.sp,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth()
        )
    }
}

@Composable
fun SubChapterRow(
    subchapter: String,
    page: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = subchapter,
            textAlign = TextAlign.Left,
            fontSize = dimensionResource(R.dimen.font_small).value.sp,
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.padding_big), top = dimensionResource(R.dimen.spacer_medium), bottom = dimensionResource(R.dimen.spacer_medium))
        )

        Text(
            text = page,
            textAlign = TextAlign.Right,
            fontSize = dimensionResource(R.dimen.font_small).value.sp,
            modifier = Modifier
                .padding(end = dimensionResource(R.dimen.padding_big), top = dimensionResource(R.dimen.spacer_medium), bottom = dimensionResource(R.dimen.spacer_medium))
                .fillMaxWidth()
        )
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewContents() {
//    BookReadingAppTheme {
//        ContentsScreen()
//    }
//}
//
//@Preview(showBackground = true, locale = "fr")
//@Composable
//fun PreviewContentsFr() {
//    BookReadingAppTheme {
//        ContentsScreen()
//    }
//}