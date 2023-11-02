package io.github.detective

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import io.github.detective.presentation.CameraScreen
import io.github.detective.presentation.HomeScreen
import io.github.detective.ui.theme.DetectiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DetectiveTheme {
                App()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun App() {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var showCameraScreen by remember(cameraPermissionState) {
        mutableStateOf(cameraPermissionState.hasPermission)
    }

    if (showCameraScreen){
       CameraScreen()
    }else{
        HomeScreen(cameraPermissionState)
    }

    if (cameraPermissionState.hasPermission){
        showCameraScreen = true
    }

}
