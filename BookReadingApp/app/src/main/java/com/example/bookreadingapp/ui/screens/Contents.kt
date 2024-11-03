package com.example.bookreadingapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookreadingapp.R
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme

@Composable
fun ContentsScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.contents),
            fontSize = dimensionResource(R.dimen.font_big).value.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        )

        // THIS IS ALL PLACEHOLDER
        Column(
            modifier = Modifier
        ) {
            ChapterRow("Introduction", "I")

            ChapterRow("Chapter 1", "1")
            SubChapterRow(subchapter = "Subchapter 1", page = "2")
            SubChapterRow(subchapter = "Subchapter 2", page = "5")
            SubChapterRow(subchapter = "Subchapter 3", page = "12")

            ChapterRow("Chapter 2", "15")
            SubChapterRow(subchapter = "Subchapter 1", page = "16")
            SubChapterRow(subchapter = "Subchapter 2", page = "25")
            SubChapterRow(subchapter = "Subchapter 3", page = "29")

            ChapterRow("Chapter 3", "36")
            SubChapterRow(subchapter = "Subchapter 1", page = "37")
            SubChapterRow(subchapter = "Subchapter 2", page = "45")
            SubChapterRow(subchapter = "Subchapter 3", page = "53")

            ChapterRow("Conclusion", "54")
        }
    }
}

@Composable
fun ChapterRow(
    chapter: String,
    page: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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

@Preview(showBackground = true)
@Composable
fun PreviewContents() {
    BookReadingAppTheme {
        ContentsScreen()
    }
}