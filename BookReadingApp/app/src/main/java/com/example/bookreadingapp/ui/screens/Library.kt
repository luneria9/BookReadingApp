package com.example.bookreadingapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bookreadingapp.R
import com.example.bookreadingapp.data.entities.Books
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.ui.theme.Typography
import com.example.bookreadingapp.viewModels.ReadingAppViewModel

// Referred to https://developer.android.com/codelabs/basic-android-kotlin-compose-material-theming#6
@Composable
fun LibraryScreen(navController: NavController, viewModel: ReadingAppViewModel) {
    // Observe the list of books from the ViewModel
    val books by viewModel.allBooks.observeAsState(emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LibraryTitle()
            BookGrid(books, navController)

            // Sample URLs and Titles for Download Buttons
            val bookTitles = stringArrayResource(R.array.book_titles)
            val bookUrls = stringArrayResource(R.array.book_urls)

            for (i in bookTitles.indices) {
                DownloadButton(
                    bookTitle = bookTitles[i],
                    onClick = {
                        downloadBook(bookUrls[i], bookTitles[i], viewModel = viewModel)
                    }
                )
            }
            Book(navController = navController)
        }
    }
}

// The header title that displays Library indicating this is the library screen
@Composable
fun LibraryTitle(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.library), fontSize = dimensionResource(R.dimen.font_big).value.sp)
    }
}

// Function to display the grid of books
@Composable
fun BookGrid(books: List<Books>, navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium))
    ) {
        items(books) {
            BookItem(it) {
                navController.navigate(NavRoutes.Contents.route)
            }
        }
    }
}

// Composable function to display buttons for navigation
@Composable
fun Book(navController: NavController) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        ContentsButton(navController = navController)
        ReadingButton(navController = navController)
    }
}

// Button for navigating to the reading screen
@Composable
fun ReadingButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(NavRoutes.Reading.route) },
        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
    ) {
        Text(
            text = stringResource(R.string.read),
            style = Typography.labelLarge
        )
    }
}

// Button for navigating to the contents screen
@Composable
fun ContentsButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(NavRoutes.Contents.route) },
        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_medium))
    ) {
        Text(
            text = stringResource(R.string.table_of_contents),
            style = Typography.labelLarge
        )
    }
}

// Composable function for individual book items
@Composable
fun BookItem(
    book: Books,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.spacer_small))
            .clickable(onClick = onClick)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = book.title, style = Typography.labelLarge)
            Text(text = book.author, style = Typography.labelLarge)
            Text(text = book.subject, style = Typography.labelMedium)
            Text(text = book.date, style = Typography.labelSmall)
        }
    }
}

@Composable
fun DownloadButton(
    bookTitle: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick
    ) {
        Text(text = stringResource(R.string.download, bookTitle))
    }
}

private fun downloadBook(url: String, fileName: String, viewModel: ReadingAppViewModel) {
    val zipFileName = url.substringAfterLast("/")

    viewModel.downloadUnzip(url, zipFileName, fileName)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLibraryScreen() {
    BookReadingAppTheme {
        val navController = rememberNavController()
        LibraryScreen(navController = navController, viewModel = viewModel())
    }
}
