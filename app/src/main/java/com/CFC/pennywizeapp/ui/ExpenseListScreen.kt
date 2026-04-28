package com.CFC.pennywizeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.CFC.pennywizeapp.models.EntryType
import com.CFC.pennywizeapp.viewmodel.ExpenseListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(viewModel: ExpenseListViewModel) {
    val datePickerState = rememberDatePickerState()
    val filteredEntries by viewModel.filteredExpenses.collectAsState(initial = emptyList())

    // Update the filter whenever the calendar selection changes
    LaunchedEffect(datePickerState.selectedDateMillis) {
        viewModel.filterByDate(datePickerState.selectedDateMillis)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 1. Calendar Section (Fixed height to allow list below)
        Surface(
            modifier = Modifier.fillMaxWidth().height(380.dp),
            shadowElevation = 2.dp,
            color = Color.White
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                title = null,
                headline = null
            )
        }

        // 2. Filtered Results Section
        Text(
            text = "Transactions",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        if (filteredEntries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No transactions for this date", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredEntries) { (entry, categoryName) ->
                    ListItem(
                        headlineContent = { Text(entry.note ?: "No Note") },
                        supportingContent = { Text(categoryName) },
                        trailingContent = {
                            val prefix = if (entry.type == EntryType.INCOME) "+" else "-"
                            val color = if (entry.type == EntryType.INCOME) Color(0xFF2E7D32) else Color.Red

                            Text(
                                text = "${prefix}R${"%.2f".format(entry.amount)}",
                                color = color,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                }
            }
        }
    }
}