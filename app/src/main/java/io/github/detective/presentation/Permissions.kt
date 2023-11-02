package io.github.detective.presentation

import android.app.Activity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import io.github.detective.R


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HandleCameraPermissions(
    permissionState: PermissionState
){
    val context = LocalContext.current
    if (!permissionState.hasPermission && permissionState.shouldShowRationale) {
        RequireCameraPermission(cameraPermissionState = permissionState){
            (context as Activity).finish()
        }
    }

}



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequireCameraPermission(
    cameraPermissionState: PermissionState,
    onDismiss: () -> Unit
) {
    if (!cameraPermissionState.hasPermission) {
        PermissionAlertDialog(
            text = stringResource(R.string.grant_camera_permission_request),
            onDismiss = onDismiss,
            onConfirm = { cameraPermissionState.launchPermissionRequest() }
        )
    }

}


@Composable
fun PermissionAlertDialog(
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            })
            { Text(text = stringResource(id = R.string.proceed)) }
        },
        title = {
            Text(text = stringResource(id = R.string.permission_text))
        },
        text = {
            Text(text = text)
        },
    )

}
