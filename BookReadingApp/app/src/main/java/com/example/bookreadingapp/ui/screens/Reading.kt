package com.example.bookreadingapp.ui.screens

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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

@Composable
fun ReadingScreen(
    readingMode: Boolean,
    onReadingCheck: (Boolean) -> Unit,
    preferences: SharedPreferences,
) {
    val test = preferences.getString("test", "123")

    if (test != null) {
        Log.d("TEST", test)
    } else {
        Log.d("TEST", "NULL2")
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.book_title),
            fontSize = dimensionResource(R.dimen.font_big).value.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .align(Alignment.TopCenter)
        )

        // PLACEHOLDER TEXT UNTIL BOOK PARSING IS IMPLEMENTED
        Text(
            text = " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras eu maximus urna. Cras luctus nulla in sollicitudin imperdiet. Donec venenatis pharetra felis, et ultricies urna egestas non. Aenean volutpat molestie est," +
                    " ut semper est aliquam eu. Proin pharetra ligula quis congue hendrerit. Maecenas ut purus nec urna feugiat semper. Sed tempor euismod nunc non vulputate. Donec feugiat urna ac ex pellentesque egestas. Suspendisse faucibus risus eget tortor maximus, in cursus nisl feugiat. Nullam ac lectus magna. Integer ac tempus ante.\n",
            fontSize = dimensionResource(R.dimen.font_medium_small).value.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(dimensionResource(R.dimen.padding_medium))
        )

        ReadingMode(
            readingMode = readingMode,
            onReadingCheck = onReadingCheck,
            modifier = Modifier.align(Alignment.BottomCenter)
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

@Preview(showBackground = true)
@Composable
fun ReadingScreenPreview() {
    BookReadingAppTheme {
        ReadingScreen(false, {}, Activity().getPreferences(Context.MODE_PRIVATE))
    }
}

@Composable
@Preview(showBackground = true, locale = "fr")
fun ReadingScreenPreviewFr() {
    BookReadingAppTheme {
        val navController = rememberNavController()
        ReadingScreen(false, {}, Activity().getPreferences(Context.MODE_PRIVATE))
    }
}