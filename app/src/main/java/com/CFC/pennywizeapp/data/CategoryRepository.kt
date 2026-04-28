package com.CFC.pennywizeapp.data

import com.CFC.pennywizeapp.models.Category
import com.CFC.pennywizeapp.models.CategoryType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CategoryRepository {
    private val _categories = MutableStateFlow(
        listOf(
            Category(id = "1", name = "Groceries", type = CategoryType.EXPENSE, color = "#FF5733", icon = "shopping_cart"),
            Category(id = "2", name = "Transport", type = CategoryType.EXPENSE, color = "#33FF57", icon = "directions_bus"),
            Category(id = "3", name = "Salary", type = CategoryType.INCOME, color = "#3357FF", icon = "attach_money")
        )
    )

    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
}