package io.github.detective.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.work.await
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.launch
import java.util.*


class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
): LifecycleEventObserver {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val executor = ContextCompat.getMainExecutor(context)
    private var cameraListener: CameraListener? = null
    private val cameraPreview = PreviewView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        scaleType = PreviewView.ScaleType.FILL_START
        keepScreenOn = true
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event){
            Lifecycle.Event.ON_CREATE -> {
                cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    cameraProviderFuture.get()
                }, executor)
            }
            else -> Unit
        }
    }

    fun setListener(cameraListener: CameraListener){
        this.cameraListener = cameraListener
    }

    @SuppressLint("RestrictedApi")
    fun initCamera(isFrontLens: Boolean = false) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val cameraProvider = cameraProviderFuture.await()
                cameraProvider.unbindAll()
                val lens = if (isFrontLens) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK


                //Select a camera lens
                val cameraSelector: CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lens)
                    .build()
                val resolutionSelector = ResolutionSelector.Builder()
                    .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                    .build()

                //Create Preview use case
                val preview: Preview = Preview.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .setTargetRotation(cameraPreview.display.rotation)
                    .build()
                    .apply {
                        setSurfaceProvider(executor, cameraPreview.surfaceProvider)
                    }

                //Create Video Capture use case
                val imageAnalysis = ImageAnalysis.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .setTargetRotation(cameraPreview.display.rotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
                    .also {
                        it.setAnalyzer(executor) { image ->
                            cameraListener?.onDetect(image)
                        }
                    }

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            }
        }
    }

    fun getCameraPreview() = cameraPreview

    //Callback to return result
    interface CameraListener{
        fun onDetect(image: ImageProxy)
    }

}