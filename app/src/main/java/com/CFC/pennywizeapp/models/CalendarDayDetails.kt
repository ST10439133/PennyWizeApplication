package com.CFC.pennywizeapp.models

data class CalendarDayDetails(
    val date: Long,
    val dailyTransactions: List<Expense>,
    val dailyIncome: List<Income>,
    val note: String? = null // For the "Notes" button in the calendar view
)