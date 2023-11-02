package io.github.detective.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import io.github.detective.R
import io.github.detective.designsystem.PrimaryButton

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(permissionState: PermissionState){
    HandleCameraPermissions(permissionState)
    HomeContent(permissionState)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun HomeContent(
    permissionState: PermissionState
){
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "icon"
        )
        Text(
            text = "Welcome to Detective",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Your realtime object detection app. \n Tap below to continue",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(text = "Grant permissions") {
            permissionState.launchPermissionRequest()
        }
        Spacer(modifier = Modifier.weight(1f))

    }
}