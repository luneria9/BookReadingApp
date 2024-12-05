package com.example.bookreadingapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookreadingapp.R
import com.example.bookreadingapp.data.entities.Chapters
import com.example.bookreadingapp.data.entities.Pages
import com.example.bookreadingapp.data.entities.SubChapters
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.ui.theme.Typography
import com.example.bookreadingapp.viewModels.ReadingAppViewModel

@Composable
fun SearchScreen(viewModel: ReadingAppViewModel, navController: NavController) {
    // State for storing the user's search query
    val searchQuery = remember { mutableStateOf("") }

    // Observing search results from the ViewModel
    val searchResultsChapters by viewModel.searchResultsChapters.observeAsState(emptyList())
    val searchResultsSubChapters by viewModel.searchResultsSubChapters.observeAsState(emptyList())
    val searchResultsPages by viewModel.searchResultsPages.observeAsState(emptyList())
    val selectedResult = remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HeaderTitle()
            Spacer(Modifier.height(dimensionResource(R.dimen.spacer_medium)))

            // Check selectedBookId instead of isBookSelected
            if (viewModel.selectedBookId == null) {
                Text(text = stringResource(R.string.please_select_a_book))
            } else {
                SearchBar(
                    searchQuery.value,
                    onDone = {
                        selectedResult.value?.let { (bookId, chapterId) ->
                            navController.navigate(NavRoutes.Reading.createRoute(bookId, chapterId))
                        }
                    }
                ) { query ->
                    searchQuery.value = query
                    viewModel.performSearch(query)
                }
                // Display the search results list
                SearchResultsList(
                    searchResultsChapters,
                    searchResultsSubChapters,
                    searchResultsPages,
                    selectedResult
                )
            }
        }
    }
}

// The header title that displays Search indicating this is the search screen
@Composable
fun HeaderTitle(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(R.string.search), fontSize = dimensionResource(R.dimen.font_big).value.sp)
    }
}

// Composable function for the search bar input
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onDone: () -> Unit, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        singleLine = true,
        shape = shapes.large,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(containerColor = colorScheme.surface),
        onValueChange = { onQueryChange(it) },
        label = { Text(stringResource(R.string.search_for_books)) },
        isError = false,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() })
    )
}

// Composable function for displaying the search results list
@Composable
fun SearchResultsList(
    chapters: List<Chapters>,
    subChapters: List<SubChapters>,
    pages: List<Pages>,
    selectedResult: MutableState<Pair<Int, Int>?>
) {
    Column {
        // Check if all lists are empty
        if (chapters.isEmpty() && subChapters.isEmpty() && pages.isEmpty()) {
            Text(
                text = "No searches found",
                style = Typography.labelLarge,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            // Display chapters
            chapters.forEach { chapter ->
                Text(
                    text = chapter.title,
                    modifier = Modifier.clickable {
                        selectedResult.value = Pair(chapter.bookId, chapter.id)
                    }
                )
            }
            // Display subchapters
            subChapters.forEach { subChapter ->
                val bookId = chapters.find { it.id == subChapter.chapterId }?.bookId
                if (bookId != null) {
                    Text(
                        text = subChapter.title,
                        modifier = Modifier.clickable {
                            selectedResult.value = Pair(bookId, subChapter.chapterId)
                        }
                    )
                }
            }
            // Display pages
            pages.forEach { page ->
                val subChapter = subChapters.find { it.id == page.subchapterId }
                val bookId = chapters.find { it.id == subChapter?.chapterId }?.bookId
                if (bookId != null) {
                    Text(
                        text = page.contents,
                        modifier = Modifier.clickable {
                            selectedResult.value = Pair(bookId, page.subchapterId)
                        }
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SearchScreenPreview() {
//    SearchScreen()
//}
//
//@Composable
//@Preview(showBackground = true, locale = "fr")
//fun SearchScreenPreviewFr() {
//    BookReadingAppTheme {
//        SearchScreen()
//    }
//}
