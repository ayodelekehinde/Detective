package io.github.detective.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.detective.ui.theme.DetectiveTheme

@Composable
@Preview(
    device = "id:pixel_4_xl", showSystemUi = true,
    backgroundColor = 0xFFFFFFFF, showBackground = true
)
fun PreviewText(){
    DetectiveTheme {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

        }
    }
}