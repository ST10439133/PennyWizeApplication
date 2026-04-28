package com.CFC.pennywizeapp.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.CFC.pennywizeapp.utils.CameraPermissionHandler
import com.CFC.pennywizeapp.utils.imageProxyToBitmap
import com.CFC.pennywizeapp.utils.saveImageToGallery

@Composable
fun CameraScreen(
    onImageCaptured: (Bitmap) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var cameraReady by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    CameraPermissionHandler(
        onGranted = {
            cameraReady = true
            showPermissionDeniedDialog = false
        },
        onDenied = {
            cameraReady = false
            showPermissionDeniedDialog = true
        },
        onPermanentlyDenied = {
            cameraReady = false
            showPermissionDeniedDialog = true
        }
    )

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { onBackPressed() },
            title = { Text("Camera Permission Required") },
            text = {
                Text("PennyWize needs camera access to take photos of expense receipts.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:${context.packageName}")
                        context.startActivity(intent)
                        onBackPressed()
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = onBackPressed) {
                    Text("Cancel")
                }
            }
        )
    }
    else if (cameraReady) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(surfaceProvider)
                            }

                            imageCapture = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build()

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        imageCapture?.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    val bitmap = imageProxyToBitmap(image)
                                    image.close()
                                    bitmap?.let {
                                        // Save to gallery using MediaStore
                                        saveImageToGallery(context, it)
                                        onImageCaptured(it)
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    exception.printStackTrace()
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .size(80.dp),
                    shape = CircleShape
                ) {
                    Text("Photo", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(onClick = onBackPressed) {
                    Text("Cancel")
                }
            }
        }
    }
}