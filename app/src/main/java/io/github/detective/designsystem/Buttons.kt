package io.github.detective.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
private fun detectivePrimaryButtonColors(
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
): ButtonColors {

    return ButtonDefaults.buttonColors(
        containerColor = backgroundColor,
        contentColor = contentColor,
        disabledContentColor = Gray
    )
}

@Composable
private fun DetectiveButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true,
    colors: ButtonColors = detectivePrimaryButtonColors()
) {

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .then(modifier),
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(size = 25.dp),
        colors = colors,
        contentPadding = PaddingValues(horizontal = 5.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onClick: () -> Unit,
) {

    DetectiveButton(
        onClick = onClick,
        text = text,
        modifier = modifier,
        enabled = enabled
    )
}
