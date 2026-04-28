package com.CFC.pennywizeapp.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.CFC.pennywizeapp.models.*
import com.CFC.pennywizeapp.utils.compressBitmap
import com.CFC.pennywizeapp.utils.resizeBitmap
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(
    categories: List<Category>,
    onSave: (Entry) -> Unit,
    onBack: () -> Unit
) {

    var selectedType by remember { mutableStateOf(EntryType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var expandedType by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    // CAMERA & GALLERY states (replaces file picker)
    var showCamera by remember { mutableStateOf(false) }
    var receiptImage by remember { mutableStateOf<Bitmap?>(null) }
    var selectedAttachmentUri by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Gallery picker (replaces old file picker)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = context.contentResolver.openInputStream(it)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
                bitmap?.let { resizedBitmap ->
                    val processedBitmap = resizeBitmap(resizedBitmap, 1024, 1024)
                    receiptImage = compressBitmap(processedBitmap)
                    selectedAttachmentUri = saveBitmapToTempUri(context, receiptImage!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 📅 DATE PICKER
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Show camera screen if needed
    if (showCamera) {
        CameraScreen(
            onImageCaptured = { bitmap ->
                val processedBitmap = resizeBitmap(bitmap, 1024, 1024)
                receiptImage = compressBitmap(processedBitmap)
                selectedAttachmentUri = saveBitmapToTempUri(context, receiptImage!!)
                showCamera = false
            },
            onBackPressed = { showCamera = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Entry") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // 🔽 TYPE DROPDOWN
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType }
                ) {
                    OutlinedTextField(
                        value = selectedType.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        EntryType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    selectedType = type
                                    expandedType = false
                                    selectedCategory = null // reset category
                                }
                            )
                        }
                    }
                }

                // 🔽 CATEGORY DROPDOWN (FIXED HERE)
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        placeholder = { Text("Select Category") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {

                        val mappedType = runCatching {
                            CategoryType.valueOf(selectedType.name)
                        }.getOrNull()

                        categories
                            .filter { it.type == mappedType }
                            .forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategory = category
                                        expandedCategory = false
                                    }
                                )
                            }
                    }
                }

                // 💰 AMOUNT
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (R)") },
                    placeholder = { Text("e.g. 500") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 📝 NOTE
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Reason / Note") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 📅 DATE FIELD
                OutlinedTextField(
                    value = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(Date(selectedDate)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                // 📸 RECEIPT IMAGE SECTION (Replaces old file picker)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Receipt Image",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            "Take a photo or select from gallery",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (receiptImage != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    bitmap = receiptImage!!.asImageBitmap(),
                                    contentDescription = "Receipt image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                IconButton(
                                    onClick = {
                                        receiptImage = null
                                        selectedAttachmentUri = null
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove image",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showCamera = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Take Photo")
                            }

                            Button(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gallery")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ✅ SAVE BUTTON
                Button(
                    onClick = {
                        val amountValue = amount.toDoubleOrNull()

                        if (selectedCategory != null && amountValue != null) {
                            val entry = Entry(
                                amount = amountValue,
                                categoryId = selectedCategory!!.id,
                                type = selectedType,
                                note = note,
                                timestamp = selectedDate,
                                attachmentUri = selectedAttachmentUri
                            )
                            onSave(entry)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("ADD")
                }
            }
        }
    }

    // 📅 DATE PICKER DIALOG
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    selectedDate =
                        datePickerState.selectedDateMillis ?: selectedDate
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// Helper function to save bitmap to temp URI for storage
private fun saveBitmapToTempUri(context: android.content.Context, bitmap: Bitmap): String? {
    return try {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bytes)
        val path = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
        val dir = java.io.File(path, "PennyWize")
        if (!dir.exists()) dir.mkdirs()

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(java.util.Date())
        val file = java.io.File(dir, "receipt_$timeStamp.jpg")
        java.io.FileOutputStream(file).use { it.write(bytes.toByteArray()) }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}