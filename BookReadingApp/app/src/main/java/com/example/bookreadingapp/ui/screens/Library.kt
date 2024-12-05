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
import androidx.compose.foundation.layout.Spacer
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
import com.example.bookreadingapp.R
import com.example.bookreadingapp.data.entities.Books
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.ui.theme.Typography
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

// Referred to https://developer.android.com/codelabs/basic-android-kotlin-compose-material-theming#6
// Displays the main library screen with a list of books
@Composable
fun LibraryScreen(navController: NavController, viewModel: ReadingAppViewModel) {
    val scope = rememberCoroutineScope()
    val books by viewModel.allBooks.observeAsState(listOf())

    LibraryScreenContent(
        books = books,
        navController = navController,
        viewModel = viewModel,
        scope = scope
    )
}

// Displays the content of the Library Screen including book list and download section
@Composable
fun LibraryScreenContent(
    books: List<Books>,
    navController: NavController,
    viewModel: ReadingAppViewModel,
    scope: CoroutineScope
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_small)),
        contentAlignment = Alignment.TopCenter
    ) {
        LibraryLayout(
            books = books,
            navController = navController,
            viewModel = viewModel,
            scope = scope
        )
    }
}

// Lays out the main components of the Library Screen
@Composable
fun LibraryLayout(
    books: List<Books>,
    navController: NavController,
    viewModel: ReadingAppViewModel,
    scope: CoroutineScope
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LibraryContent(
            books = books,
            navController = navController,
            modifier = Modifier.weight(1f),
            viewModel = viewModel
        )
        DownloadSection(viewModel, scope)
    }
}

// Displays the library content with title and grid of books
@Composable
fun LibraryContent(
    books: List<Books>,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ReadingAppViewModel
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LibraryTitle()

        if (books.isEmpty()) {
            Text(
                text = stringResource(R.string.downloading),
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.spacer_big)
                )
            )
        } else {
            BookGrid(
                books = books,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

// Displays a book card with title, author, and cover image
@Composable
fun BookCard(
    book: Books,
    onBookClick: () -> Unit,
    viewModel: ReadingAppViewModel
) {
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
        BookCardContent(book, viewModel)
    }
}

// Displays the content inside a book card: cover and book info sections
@Composable
fun BookCardContent(book: Books, viewModel: ReadingAppViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        BookCoverSection(book, Modifier.weight(0.7f), viewModel)
        BookInfoSection(book, Modifier.weight(0.3f))
    }
}

// Displays the book cover image section
@Composable
fun BookCoverSection(book: Books, modifier: Modifier = Modifier, viewModel: ReadingAppViewModel) {
    val context = LocalContext.current
    val bookNames = stringArrayResource(R.array.book_titles)
    var bookDir = "${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/"
    bookNames.forEach { b ->
        if(book.title.contains(b)){
            bookDir += b
        }
    }
    val coverPath = viewModel.getCoverImagePath(bookDir)

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        BookCover(coverPath)
    }
}

// Displays the section with book information
@Composable
fun BookInfoSection(book: Books, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        BookInformation(book)
    }
}

// Displays detailed information about the book
@Composable
fun BookInformation(book: Books) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacer_small))
    ) {
        BookTitle(book.title)
        BookAuthor(book.author)
        BookSubject(book.subject)
        BookDate(book.date)
    }
}

// Displays the book title
@Composable
fun BookTitle(title: String) {
    Text(
        text = title,
        style = Typography.titleMedium.copy(
            fontSize = dimensionResource(id = R.dimen.font_very_small).value.sp
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
    )
}

// Displays the author of the book
@Composable
fun BookAuthor(author: String) {
    Text(
        text = "by $author",
        style = Typography.bodySmall.copy(
            fontSize = dimensionResource(id = R.dimen.font_very_small).value.sp
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
    )
}

// Displays the book's subject
@Composable
fun BookSubject(subject: String) {
    Text(
        text = subject,
        style = Typography.bodySmall.copy(
            fontSize = dimensionResource(id = R.dimen.font_very_small).value.sp
        ),
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center
    )
}

// Displays the book's publication date
@Composable
fun BookDate(date: String) {
    Text(
        text = date,
        style = Typography.labelSmall.copy(
            fontSize = dimensionResource(id = R.dimen.font_very_small).value.sp
        ),
        color = MaterialTheme.colorScheme.tertiary,
        textAlign = TextAlign.Center
    )
}

// Section with buttons to download books
@Composable
fun DownloadSection(viewModel: ReadingAppViewModel, scope: CoroutineScope) {
    val bookTitles = stringArrayResource(R.array.book_titles)
    val bookUrls = stringArrayResource(R.array.book_urls)
    val downloadedTitles by viewModel.downloadedTitles.observeAsState(mutableListOf())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        bookTitles.forEachIndexed { index, title ->
            if (index > 2 && !downloadedTitles.contains(title)) {
                DownloadButton(
                    title = title,
                    url = bookUrls[index],
                    scope = scope,
                    viewModel = viewModel
                )
            }
        }
        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacer_medium)))
    }
}

// Button to download a specific book
@Composable
fun DownloadButton(title: String, url: String, scope: CoroutineScope, viewModel: ReadingAppViewModel) {
    Button(
        onClick = {
            scope.launch { downloadBook(url, title, viewModel) }
            viewModel.addDownload(title)
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(vertical = dimensionResource(id = R.dimen.spacer_small)),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.spacer_small))
    ) {
        Text(
            text = stringResource(R.string.download, title),
            fontSize = dimensionResource(id = R.dimen.font_small).value.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Displays the book cover image
@Composable
fun BookCover(coverPath: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_small))
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
                    .padding(dimensionResource(id = R.dimen.padding_small)),
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

// The header title that displays Library indicating this is the library screen
@Composable
fun LibraryTitle(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.library), fontSize = dimensionResource(R.dimen.font_big).value.sp)
    }
}

// Function to display the grid of books
@Composable
fun BookGrid(
    books: List<Books>,
    navController: NavController,
    viewModel: ReadingAppViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(books) { book ->
            BookCard(
                book = book,
                onBookClick = {
                    navController.navigate(NavRoutes.Contents.createRoute(book.id))
                },
                viewModel = viewModel
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

// Created book download function for downloading book files
suspend fun downloadBook(url: String, fileName: String, viewModel: ReadingAppViewModel) {
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