package io.github.detective.presentation

import android.graphics.Bitmap
import android.os.Build
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.FlipCameraAndroid
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import io.github.detective.dectector.Categorizer
import io.github.detective.dectector.Category
import io.github.detective.dectector.ObjectDetectorHelper
import io.github.detective.utils.rotate
import io.github.detective.utils.saveImageToGallery
import io.github.detective.utils.screenshot
import io.github.detective.utils.showMessage
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.lang.Float.max
import kotlin.math.roundToInt


@Composable
fun CameraScreen(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var widthHeight by remember {
        mutableStateOf(0 to 0)
    }
    var threshold by remember {
        mutableStateOf(0.5f)
    }
    var mResults by remember {
        mutableStateOf(listOf<Detection>())
    }
    var isFrontLens by remember {
        mutableStateOf(false)
    }
    val objectDetector = remember {
        ObjectDetectorHelper(context = context, threshold = threshold)
    }
    val cameraManager = remember(context) {
        CameraManager(context, lifecycleOwner)
    }
    val view =  LocalView.current
    var composableBounds by remember {
        mutableStateOf<Rect?>(null)
    }
    var imageBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    var showSavedUi by remember {
        mutableStateOf(false)
    }



    LaunchedEffect(key1 = cameraManager, objectDetector){
        cameraManager.setListener(object: CameraManager.CameraListener{
            override fun onDetect(image: ImageProxy) {
                imageBitmap = image.toBitmap()
                val bitmapBuffer = Bitmap.createBitmap(
                    image.width,
                    image.height,
                    Bitmap.Config.ARGB_8888
                )
                image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

                val imageRotation = image.imageInfo.rotationDegrees
                objectDetector.detect(bitmapBuffer, imageRotation)
            }
        }
        )
        objectDetector.setListener(object: ObjectDetectorHelper.DetectorListener {
            override fun onInitialized() {
                cameraManager.initCamera(isFrontLens)
            }

            override fun onError(error: String) {
                context.showMessage(error)
            }

            override fun onResults(
                results: List<Detection>?,
                imageHeight: Int,
                imageWidth: Int
            ) {
                results?.let {
                    mResults = it
                }
                widthHeight = imageWidth to imageHeight
            }

        })
    }


    Box(
        modifier = modifier.fillMaxSize()
    ){
        if (showSavedUi){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        composableBounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            it.boundsInWindow()
                        } else {
                            it.boundsInRoot()
                        }
                    }
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = imageBitmap!!.rotate(90F).asImageBitmap(),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
                DetectionCanvas(
                    modifier = Modifier.fillMaxSize(),
                    widthHeight = widthHeight,
                    results = mResults
                )
            }
        }else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                CameraPreviewView(recorderManager = cameraManager)
                DetectionCanvas(
                    modifier = Modifier.fillMaxSize(),
                    widthHeight = widthHeight,
                    results = mResults
                )
            }
            DetectionCount(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                count = mResults.size
            )
            BottomActions(
                modifier = Modifier.align(Alignment.BottomCenter),
                threshold = threshold,
                onThresholdChange = {
                    threshold = it
                    objectDetector.threshold = threshold
                    objectDetector.clearObjectDetector()
                },
                onLensSwitch = {
                    isFrontLens = !isFrontLens
                    cameraManager.initCamera(isFrontLens)
                },
                onCapture = {
                    showSavedUi = true
                }
            )
        }
    }

    //Take a snapshot and save to gallery
    LaunchedEffect(key1 = showSavedUi){
        if (showSavedUi){
            val bounds = composableBounds ?: return@LaunchedEffect
            val bitmap = view.screenshot(bounds)
            if (bitmap != null) {
                context.saveImageToGallery(bitmap)
                showSavedUi = false
            }
        }
    }
}


@Composable
private fun DetectionCount(
    modifier: Modifier = Modifier,
    count: Int
){
    Text(
        text = "Objects: $count",
        style = MaterialTheme.typography.bodyLarge.copy(
            color = Color.White
        ),
        modifier = modifier
    )
}

@Composable
private fun DetectionCanvas(
    modifier: Modifier = Modifier,
    widthHeight: Pair<Int, Int>,
    results: List<Detection>
){
    val textMeasurer = rememberTextMeasurer()
    val getBoundColor: (String) -> Color = { label ->
        when(Categorizer.categorize(label)){
           Category.Animal -> Color.Red
           Category.Things -> Color.Blue
           Category.Plants -> Color.Yellow
           Category.Person -> Color.Green
           Category.Unknown -> Color.Cyan
       }
    }
    Canvas(
        modifier = modifier
    ){
        //Scaling bounding boxes to match the preview
        val scaleFactor = max(size.width * 1f / widthHeight.first, size.height * 1f / widthHeight.second)
        val strokeSize = 8f
        results.forEach {
            val boundingBox = it.boundingBox
            val category = it.categories.first().label

            val top = boundingBox.top * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor
            val left = boundingBox.left * scaleFactor
            val right = boundingBox.right * scaleFactor

            val rect = Rect(left, top, right, bottom)

            val textSize = textMeasurer.measure(
                text = category,
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            drawRect(
                color = getBoundColor(category),
                topLeft = rect.topLeft,
                size = rect.size,
                style = Stroke(width = strokeSize)
            )
            drawRect(
                color = Color.Black,
                topLeft = Offset(left + strokeSize, top + strokeSize),
                size = textSize.size.toSize()
            )
            drawText(
                textLayoutResult = textSize,
                topLeft = Offset(left + strokeSize, top + strokeSize),
            )

        }
    }
}

@Composable
private fun BottomActions(
    modifier: Modifier = Modifier,
    threshold: Float,
    onThresholdChange: (Float) -> Unit,
    onLensSwitch: () -> Unit,
    onCapture: () -> Unit
){
    Column(modifier = modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ThresholdSlider(
            modifier = Modifier.padding(horizontal = 20.dp),
            threshold = threshold,
            onChange = onThresholdChange
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            CaptureScreenButton(modifier = Modifier.weight(2f)) {
                onCapture()
            }
            LensSwitchButton(modifier = Modifier.weight(1f), onSwitch = onLensSwitch)
        }

    }
}

@Composable
private fun LensSwitchButton(
    modifier: Modifier = Modifier,
    onSwitch: () -> Unit
){
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Sharp.FlipCameraAndroid,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .offset(x = (-50).dp)
                .size(40.dp)
                .clickable { onSwitch() }
        )
    }
}

@Composable
private fun CaptureScreenButton(
    modifier: Modifier = Modifier,
    onSwitch: () -> Unit
){
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Sharp.Lens,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .size(70.dp)
                .padding(1.dp)
                .border(3.dp, Color.White, CircleShape)
                .clickable { onSwitch() }
        )
    }
}

@Composable
private fun ThresholdSlider(
    modifier: Modifier = Modifier,
    threshold: Float,
    onChange: (Float) -> Unit
){
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Slider(
            modifier = Modifier.weight(1f),
            value = threshold,
            onValueChange = onChange
        )
        Text(
            text = threshold.toPercent(),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White
            )
        )
    }
    
}

@Composable
private fun CameraPreviewView(
    recorderManager: CameraManager
){
    AndroidView(
        factory = {
            recorderManager.getCameraPreview()
        },
        modifier = Modifier.fillMaxSize()
    )
}

fun Float.toPercent(): String {
    val percentage = this * 100
    return "${percentage.roundToInt()}%"
}