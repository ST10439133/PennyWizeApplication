package com.CFC.pennywizeapp.models

data class CalendarDayDetails(
    val date: Long,
    val entries: List<Entry>, // unified list
    val note: String? = null
)