package com.example.bookreadingapp.ui.screens

import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.bookreadingapp.data.entities.Books
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.ui.theme.Typography
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.bookreadingapp.R
import java.io.File

// Referred to https://developer.android.com/codelabs/basic-android-kotlin-compose-material-theming#6
@Composable
fun LibraryScreen(navController: NavController, viewModel: ReadingAppViewModel) {
    val scope = rememberCoroutineScope()
    val books by viewModel.allBooks.observeAsState(listOf())

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
            verticalArrangement = Arrangement.Top
        ) {
            LibraryTitle()
            BookGrid(books = books, navController = navController)
            DownloadSection(viewModel, scope)
        }
    }
}

@Composable
fun DownloadSection(viewModel: ReadingAppViewModel, scope: CoroutineScope) {
    val bookTitles = stringArrayResource(R.array.book_titles)
    val bookUrls = stringArrayResource(R.array.book_urls)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_small)),
        verticalArrangement = Arrangement.spacedBy(8.dp), // Spacing between buttons
        horizontalAlignment = Alignment.CenterHorizontally // Center the buttons
    ) {
        for (i in bookTitles.indices) {
            DownloadButton(
                bookTitle = bookTitles[i],
                onClick = {
                    scope.launch {
                        downloadBook(bookUrls[i], bookTitles[i], viewModel)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
            )
        }
    }
}

@Composable
fun BookCard(
    book: Books,
    onBookClick: () -> Unit
) {
    val context = LocalContext.current
    val coverPath = remember(book.id) {
        val bookDir = "${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/${book.title}"
        File(bookDir).walkTopDown()
            .find { file ->
                file.name.endsWith("-cover.png", ignoreCase = true) ||
                        file.name.endsWith("-cover.jpg", ignoreCase = true)
            }
            ?.absolutePath ?: ""
    }

    Card(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_small))
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable(onClick = onBookClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.spacer_small)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_small)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacer_small))
        ) {
            BookCover(coverPath)
            BookInformation(book)
        }
    }
}

@Composable
fun BookCover(coverPath: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(dimensionResource(id = R.dimen.spacer_small))
            ),
        contentAlignment = Alignment.Center
    ) {
        if (coverPath.isNotEmpty() && File(coverPath).exists()) {
            AsyncImage(
                model = coverPath,
                contentDescription = "Book Cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f),
                contentScale = ContentScale.Fit
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.book_icon),
                contentDescription = "Book Cover",
                modifier = Modifier.size(dimensionResource(id = R.dimen.font_big))
            )
        }
    }
}

@Composable
fun BookInformation(book: Books) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BookDetails(book)
        BookMetadata(book)
    }
}

@Composable
fun BookDetails(book: Books) {
    Text(
        text = book.title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = dimensionResource(id = R.dimen.font_medium_small).value.sp
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
    )

    Text(
        text = "by ${book.author}",
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = dimensionResource(id = R.dimen.font_small).value.sp
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
    )
}

@Composable
fun BookMetadata(book: Books) {
    Text(
        text = book.subject,
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = dimensionResource(id = R.dimen.font_small).value.sp
        ),
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center
    )

    Text(
        text = book.date,
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = dimensionResource(id = R.dimen.font_small).value.sp
        ),
        color = MaterialTheme.colorScheme.tertiary,
        textAlign = TextAlign.Center
    )
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
        contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(books) { book ->
            BookCard(
                book = book,
                onBookClick = {
                    navController.navigate(NavRoutes.Contents.route)
                }
            )
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

@Composable
fun DownloadButton(
    bookTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.download, bookTitle),
            overflow = TextOverflow.Ellipsis
        )
    }
}

private suspend fun downloadBook(url: String, fileName: String, viewModel: ReadingAppViewModel) {
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
