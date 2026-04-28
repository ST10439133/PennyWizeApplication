package com.CFC.pennywizeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.CFC.pennywizeapp.viewmodel.BudgetGoalsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetGoalsScreen(
    viewModel: BudgetGoalsViewModel,
    onNavigateToAdd: () -> Unit
) {
    val budget by viewModel.budgetState.collectAsStateWithLifecycle()
    val income by viewModel.totalIncome.collectAsStateWithLifecycle()
    val expenses by viewModel.totalExpenses.collectAsStateWithLifecycle()

    // Use budget values from StateFlow (fallback to defaults if null)
    val minGoal = budget?.minGoal ?: 500.0
    val maxGoal = budget?.maxGoal ?: 2000.0
    val currentTotal = budget?.currentTotal ?: 0.0
    val progress = budget?.progress ?: 0f
    val status = budget?.status ?: "Within Range"

    var minText by remember(minGoal) { mutableStateOf(minGoal.toString()) }
    var maxText by remember(maxGoal) { mutableStateOf(maxGoal.toString()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Budget Analysis", fontWeight = FontWeight.SemiBold) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Add Entry")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BudgetSummaryCard("Income", income, Color(0xFF2E7D32), Modifier.weight(1f))
                BudgetSummaryCard("Expenses", expenses, Color(0xFFD32F2F), Modifier.weight(1f))
            }

            Spacer(Modifier.height(48.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 16.dp,
                    color = if (status == "Above Maximum") Color.Red else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("GOALS", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                    GoalInputField(minText, { minText = it }, "Min")
                    HorizontalDivider(Modifier.width(80.dp).padding(vertical = 8.dp))
                    GoalInputField(maxText, { maxText = it }, "Max")
                    IconButton(onClick = {
                        val min = minText.toDoubleOrNull() ?: 500.0
                        val max = maxText.toDoubleOrNull() ?: 2000.0
                        if (min > 0 && max > 0 && min <= max) {
                            viewModel.saveGoals(min, max)
                        }
                    }) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetSummaryCard(title: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = color, style = MaterialTheme.typography.labelMedium)
            Text("R${"%.2f".format(amount)}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GoalInputField(value: String, onValueChange: (String) -> Unit, label: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label) },
        modifier = Modifier.width(110.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}