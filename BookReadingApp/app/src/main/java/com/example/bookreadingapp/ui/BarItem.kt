package com.example.bookreadingapp.ui

import androidx.compose.ui.graphics.vector.ImageVector

//referenced from https://gitlab.com/crdavis/adaptivenavigationegcode/-/tree/master?ref_type=heads
data class BarItem(
    val title: String,
    val image: ImageVector,
    val route: String
)