package com.example.bookreadingapp.ui.screens

import android.util.Log
import coil.compose.rememberImagePainter
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.bookreadingapp.data.entities.Books
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.ui.theme.Typography
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.bookreadingapp.R

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

            // Grid of Books
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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

            DownloadSection(viewModel, scope)
        }
    }
}

@Composable
fun DownloadSection(viewModel: ReadingAppViewModel, scope: CoroutineScope) {
    val bookTitles = stringArrayResource(R.array.book_titles)
    val bookUrls = stringArrayResource(R.array.book_urls)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in bookTitles.indices) {
            DownloadButton(
                bookTitle = bookTitles[i],
                onClick = {
                    scope.launch {
                        downloadBook(bookUrls[i], bookTitles[i], viewModel)
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BookCard(
    book: Books,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable(onClick = onBookClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Book Cover
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder image if no cover available
                Image(
                    painter = painterResource(id = R.drawable.book_icon),
                    contentDescription = "Book Cover",
                    modifier = Modifier.size(64.dp)
                )
            }

            // Book Information
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "by ${book.author}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = book.subject,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = book.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center
                )
            }
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
fun BookGrid(books: List<Books>, navController: NavController, viewModel: ReadingAppViewModel) {
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
            .size(150.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = book.title,
                style = Typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = book.subject,
                style = Typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = book.date,
                style = Typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
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
