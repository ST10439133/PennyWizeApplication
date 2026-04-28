package com.CFC.pennywizeapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.CFC.pennywizeapp.R

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 20
) {
    Image(
        painter = painterResource(id = R.drawable.custom_logo), // Your PNG file
        contentDescription = "App Logo",
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp)),
        contentScale = ContentScale.Crop
    )
}