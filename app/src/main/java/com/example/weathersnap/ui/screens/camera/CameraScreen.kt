package com.example.weathersnap.ui.screens.camera

import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.weathersnap.utils.compressImage
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executor

@Composable
fun CameraScreen(
    onImageCaptured: (String, String, Long, Long) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isCapturing by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    imageCapture = capture

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            capture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        CameraOverlay(onClose = onClose)

        // Capture Button
        Button(
            onClick = {
                if (!isCapturing) {
                    isCapturing = true
                    captureImage(
                        context = context,
                        imageCapture = imageCapture,
                        executor = ContextCompat.getMainExecutor(context),
                        onSuccess = { originalPath ->
                            scope.launch {
                                val result = compressImage(originalPath, context.filesDir)
                                isCapturing = false
                                onImageCaptured(
                                    result.originalPath,
                                    result.compressedPath,
                                    result.originalSizeKb,
                                    result.compressedSizeKb
                                )
                            }
                        },
                        onError = { isCapturing = false }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 48.dp)
                .height(64.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            enabled = !isCapturing
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF0091EA), Color(0xFF00E676)))), // Same gradient
                contentAlignment = Alignment.Center
            ) {
                if (isCapturing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Capture", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CameraOverlay(onClose: () -> Unit) {
    // 3x3 Grid & Focus Brackets
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val linePaint = Color.White.copy(alpha = 0.3f)

        // Vertical lines
        drawLine(color = linePaint, start = Offset(width / 3, 0f), end = Offset(width / 3, height), strokeWidth = 2f)
        drawLine(color = linePaint, start = Offset(width * 2 / 3, 0f), end = Offset(width * 2 / 3, height), strokeWidth = 2f)

        // Horizontal lines
        drawLine(color = linePaint, start = Offset(0f, height / 3), end = Offset(width, height / 3), strokeWidth = 2f)
        drawLine(color = linePaint, start = Offset(0f, height * 2 / 3), end = Offset(width, height * 2 / 3), strokeWidth = 2f)

        // Center Focus Bracket
        val bracketSize = 80f
        val centerX = width / 2
        val centerY = height / 2
        val bracketColor = Color.White.copy(alpha = 0.6f)

        // Top Left Bracket
        drawLine(bracketColor, Offset(centerX - bracketSize, centerY - bracketSize), Offset(centerX - bracketSize + 30f, centerY - bracketSize), 4f)
        drawLine(bracketColor, Offset(centerX - bracketSize, centerY - bracketSize), Offset(centerX - bracketSize, centerY - bracketSize + 30f), 4f)
        // Top Right Bracket
        drawLine(bracketColor, Offset(centerX + bracketSize, centerY - bracketSize), Offset(centerX + bracketSize - 30f, centerY - bracketSize), 4f)
        drawLine(bracketColor, Offset(centerX + bracketSize, centerY - bracketSize), Offset(centerX + bracketSize, centerY - bracketSize + 30f), 4f)
        // Bottom Left Bracket
        drawLine(bracketColor, Offset(centerX - bracketSize, centerY + bracketSize), Offset(centerX - bracketSize + 30f, centerY + bracketSize), 4f)
        drawLine(bracketColor, Offset(centerX - bracketSize, centerY + bracketSize), Offset(centerX - bracketSize, centerY + bracketSize - 30f), 4f)
        // Bottom Right Bracket
        drawLine(bracketColor, Offset(centerX + bracketSize, centerY + bracketSize), Offset(centerX + bracketSize - 30f, centerY + bracketSize), 4f)
        drawLine(bracketColor, Offset(centerX + bracketSize, centerY + bracketSize), Offset(centerX + bracketSize, centerY + bracketSize - 30f), 4f)
    }

    // Top Floating Bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            .background(Color(0xFF222222).copy(alpha = 0.8f), RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(48.dp))
            Text(
                text = "Custom Camera",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.White
            )
            Surface(
                onClick = onClose,
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
            ) {
                Text(
                    "Close",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

private fun captureImage(
    context: Context,
    imageCapture: ImageCapture?,
    executor: Executor,
    onSuccess: (String) -> Unit,
    onError: () -> Unit
) {
    val photoFile = File(context.filesDir, "original_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture?.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onSuccess(photoFile.absolutePath)
            }
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onError()
            }
        }
    )
}