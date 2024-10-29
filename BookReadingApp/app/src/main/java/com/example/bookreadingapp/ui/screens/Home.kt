package com.example.bookreadingapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme

@Composable
fun HomeScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = Color.Blue,
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.Center)
            )
        }
}

@Composable
@Preview(showBackground = true)
fun PreviewHomeScreen() {
    BookReadingAppTheme {
        HomeScreen()
    }
}

