package com.example.bookreadingapp.ui.screens

import androidx.compose.foundation.horizontalScroll
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookreadingapp.R
import com.example.bookreadingapp.data.entities.Pages
import com.example.bookreadingapp.data.entities.SubChapters
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import androidx.navigation.NavController
import com.example.bookreadingapp.data.entities.Chapters

// the main reading screen function
@Composable
fun ReadingScreen(
    preferences: SharedPreferences,
    readingMode: Boolean,
    onReadingCheck: (Boolean) -> Unit,
    bookId: Int,
    chapterId: Int,
    viewModel: ReadingAppViewModel,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    // Store the last book and chapter accessed
    with(preferences.edit()) {
        putInt(stringResource(R.string.last_location_book), bookId)
        putInt(stringResource(R.string.last_location_chapter), chapterId)
        apply()
    }

    // Observe content states
    val subChapters by remember(chapterId) {
        viewModel.findSubChaptersOfChapter(chapterId)
        viewModel.searchResultsSubChapters
    }.observeAsState(initial = emptyList())

    // Observe chapters
    val chapters by remember(bookId) {
        viewModel.findChaptersFromBook(bookId)
        viewModel.searchResultsChapters
    }.observeAsState(initial = emptyList())

    // Main container for the reading screen
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        ReadingMainContent(
            modifier,
            subChapters,
            viewModel,
            readingMode,
            onReadingCheck,
            navController,
            bookId,
            chapterId,
            chapters
        )
    }
}

// contains all the main content for ReadingScreen
@Composable
private fun ReadingMainContent(
    modifier: Modifier,
    subChapters: List<SubChapters>,
    viewModel: ReadingAppViewModel,
    readingMode: Boolean,
    onReadingCheck: (Boolean) -> Unit,
    navController: NavController,
    bookId: Int,
    chapterId: Int,
    chapters: List<Chapters>
) {
    val currentChapter = chapters.find { it.id == chapterId}
    Column(
        modifier = Modifier
            .height(500.dp)
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(rememberScrollState())
    ) {
        // Display the subchapter title at the top
        if (currentChapter != null) {
            Text(
                text = currentChapter.title,
                fontSize = dimensionResource(R.dimen.font_big).value.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium))
            )
        }
        // Render the content of the chapter
        ChapterContent(
            subChapters = subChapters,
            viewModel = viewModel,
            readingMode = readingMode,
        )
    }
    Column (
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the reading mode toggle at the bottom of the screen
        ReadingMode(
            readingMode = readingMode,
            onReadingCheck = onReadingCheck,
        )
        NavigateBook(
            readingMode = readingMode,
            navController = navController,
            bookId = bookId,
            chapterId = chapterId,
            viewModel = viewModel,
            chapters
        )
    }
}

// Composable to display the content of a chapter
@Composable
fun ChapterContent(
    subChapters: List<SubChapters>,
    viewModel: ReadingAppViewModel,
    readingMode: Boolean
) {
    subChapters.forEach { subChapter ->
        // Observe pages for each subchapter
        val pages by remember(subChapter.id) {
            viewModel.getSetPagesOfSubChapter(subChapter.id)
        }.observeAsState(initial = emptyList())

        SubChapterSection(
            subChapter = subChapter,
            pages = pages,
            viewModel = viewModel,
            readingMode = readingMode
        )
    }
}

// Composable to display a subchapter and its pages
@Composable
fun SubChapterSection(
    subChapter: SubChapters,
    pages: List<Pages>,
    viewModel: ReadingAppViewModel,
    readingMode: Boolean
) {
    var displayTitle = subChapter.title
    if (displayTitle.contains("<PLACEHOLDER>")){
        displayTitle = ""
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_small))
    ) {
        // Display the subchapter title at the top
        Text(
            text = displayTitle,
            fontSize = dimensionResource(R.dimen.font_big).value.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium))
        )

        // Create a Row for each page to allow horizontal scrolling
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            pages.forEach { page ->
                PageContent(
                    page = page,
                    viewModel = viewModel,
                    readingMode = readingMode
                )
            }
        }
    }
}

// Composable to display the content of a page
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
    val replacedImage = page.contents.replace("<IMAGE>", "")
    val replacedPlaceholder = replacedImage.replace("<PLACEHOLDER>", "")

    Column(
        modifier = Modifier
            .width(380.dp)
            .padding(vertical = dimensionResource(R.dimen.padding_small))
    ) {
        Text(
            text = replacedPlaceholder,
            fontSize = textSize,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
        )

        images.forEach { image ->
            ImageContent(
                imageUrl = image.imageUrl
            )
        }
    }
}

// Composable to display an image
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
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.reading_mode),
            fontSize = dimensionResource(R.dimen.font_medium).value.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.spacer_medium)))
        Switch(
            checked = readingMode,
            onCheckedChange = onReadingCheck,
            modifier = Modifier.testTag("readingModeSwitch")
        )
    }
}

// Composable to display navigation for reading mode
@Composable
fun NavigateBook(
    readingMode: Boolean,
    navController: NavController,
    bookId: Int,
    chapterId: Int,
    viewModel: ReadingAppViewModel,
    chapters: List<Chapters>
) {


    var currentBookPosition = 0;
    chapters.forEachIndexed { index, chapter ->
        if (chapter.id == chapterId){
            currentBookPosition = index
        }
    }
    if (readingMode) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (currentBookPosition > 0) {
                Button(
                    onClick = {
                        navController.navigate(NavRoutes.Reading.createRoute(bookId,
                            chapters[currentBookPosition - 1].id))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = dimensionResource(R.dimen.padding_small)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Previous Page")
                }
            }
            if (currentBookPosition < chapters.size - 1) {
                Button(
                    onClick = {
                        navController.navigate(NavRoutes.Reading.createRoute(bookId,
                            chapters[currentBookPosition + 1].id))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = dimensionResource(R.dimen.padding_small)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Next Page")
                }
            }
        }
    }
}