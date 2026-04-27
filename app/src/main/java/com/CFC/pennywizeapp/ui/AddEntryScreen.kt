package com.CFC.pennywizeapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.CFC.pennywizeapp.models.*
import java.text.SimpleDateFormat
import java.util.*

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

    // ✅ FILE PICKER
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    // ✅ DATE PICKER
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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

            // TYPE
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
                                selectedCategory = null
                            }
                        )
                    }
                }
            }

            // CATEGORY
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
                    categories
                        .filter { it.type.name == selectedType.name }
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

            // AMOUNT
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (R)") },
                placeholder = { Text("e.g. 500") },
                modifier = Modifier.fillMaxWidth()
            )

            // NOTE
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

            // 📎 FILE PICKER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { filePickerLauncher.launch("*/*") },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Attachment")
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedFileUri?.lastPathSegment ?: "Add Document")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SAVE BUTTON
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
                            attachmentUri = selectedFileUri?.toString()
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

    // DATE PICKER DIALOG
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