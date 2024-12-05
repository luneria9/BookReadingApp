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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookreadingapp.R
import com.example.bookreadingapp.data.entities.Chapters
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.viewModels.ReadingAppViewModel

@Composable
fun ContentsScreen(
    bookId: Int,
    navController: NavController,
    viewModel: ReadingAppViewModel
) {
    // Observe chapters
    val chapters by remember(bookId) {
        viewModel.findChaptersFromBook(bookId)
        viewModel.searchResultsChapters
    }.observeAsState(initial = emptyList())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TitleText()
        ChapterList(
            chapters = chapters,
            onChapterClick = { chapterId ->
                // Navigate to reading screen when chapter is clicked
                navController.navigate(NavRoutes.Reading.createRoute(bookId, chapterId))
            }
        )
    }
}

// Displays a list of chapters as rows
@Composable
fun ChapterList(
    chapters: List<Chapters>,
    onChapterClick: (Int) -> Unit,
) {
    chapters.forEach { chapter ->
        ChapterWithSubChapters(
            chapter = chapter,
            onChapterClick = onChapterClick
        )
    }
}

// Displays a single chapter
@Composable
fun ChapterWithSubChapters(
    chapter: Chapters,
    onChapterClick: (Int) -> Unit,
) {
    ChapterRow(
        chapter = chapter.title,
        page = chapter.id.toString(),
        onClick = { onChapterClick(chapter.id) }
    )
}

// Displays the title text for the Contents screen
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

// Displays a single chapter row with its title and page
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
