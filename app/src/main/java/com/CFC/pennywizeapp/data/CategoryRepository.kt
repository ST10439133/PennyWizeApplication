package com.CFC.pennywizeapp.data

import android.content.Context
import com.CFC.pennywizeapp.data.local.DatabaseProvider
import com.CFC.pennywizeapp.models.Category
import com.CFC.pennywizeapp.models.CategoryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class CategoryRepository private constructor(private val context: Context) {

    private val database = DatabaseProvider.getDatabase(context)
    private val categoryDao = database.categoryDao()

    private val _categories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private var isInitialized = false

    init {
        loadCategories()
    }

    private fun loadCategories() {
        if (isInitialized) return
        isInitialized = true

        CoroutineScope(Dispatchers.IO).launch {
            categoryDao.getAllCategories()
                .catch { e -> e.printStackTrace() }
                .collect { categoryList ->
                    _categories.value = categoryList
                }
        }

        CoroutineScope(Dispatchers.IO).launch {
            prePopulateCategories()
        }
    }

    private suspend fun prePopulateCategories() {
        val existingCategories = categoryDao.getAllCategories().first()
        if (existingCategories.isNotEmpty()) return

        val defaultCategories = listOf(
            Category(id = "1", name = "Groceries", type = CategoryType.EXPENSE, color = "#FF5733", icon = "shopping_cart"),
            Category(id = "2", name = "Transport", type = CategoryType.EXPENSE, color = "#33FF57", icon = "directions_bus"),
            Category(id = "3", name = "Shopping", type = CategoryType.EXPENSE, color = "#E91E63", icon = "shopping_bag"),
            Category(id = "4", name = "Bills", type = CategoryType.EXPENSE, color = "#F44336", icon = "receipt"),
            Category(id = "5", name = "Entertainment", type = CategoryType.EXPENSE, color = "#9C27B0", icon = "movie"),
            Category(id = "6", name = "Health", type = CategoryType.EXPENSE, color = "#00BCD4", icon = "favorite"),
            Category(id = "7", name = "Dining Out", type = CategoryType.EXPENSE, color = "#FF5722", icon = "restaurant"),
            Category(id = "8", name = "Other", type = CategoryType.EXPENSE, color = "#9E9E9E", icon = "help"),
            Category(id = "9", name = "Salary", type = CategoryType.INCOME, color = "#3357FF", icon = "attach_money"),
            Category(id = "10", name = "Freelance", type = CategoryType.INCOME, color = "#2196F3", icon = "computer"),
            Category(id = "11", name = "Gift", type = CategoryType.INCOME, color = "#FFC107", icon = "card_giftcard"),
            Category(id = "12", name = "Investment", type = CategoryType.INCOME, color = "#009688", icon = "trending_up"),
            Category(id = "13", name = "Other Income", type = CategoryType.INCOME, color = "#9E9E9E", icon = "attach_money")
        )

        categoryDao.insertCategories(defaultCategories)
    }

    /**
     * Create a new category
     * @param name The category name
     * @param type The category type (INCOME or EXPENSE)
     * @param color Optional hex color string (auto-generated if not provided)
     * @param icon Optional icon name (defaults to "label")
     * @return The newly created Category object
     */
    suspend fun createCategory(name: String, type: CategoryType, color: String = generateRandomColor(), icon: String = "label"): Category {
        val newCategory = Category(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            color = color,
            icon = icon
        )
        categoryDao.insertCategory(newCategory)

        // Refresh the categories list to include the new category
        CoroutineScope(Dispatchers.IO).launch {
            categoryDao.getAllCategories()
                .catch { e -> e.printStackTrace() }
                .collect { categoryList ->
                    _categories.value = categoryList
                }
        }

        return newCategory
    }

    /**
     * Generate a random color for new categories
     * @return Hex color string
     */
    private fun generateRandomColor(): String {
        val colors = listOf(
            "#FF5733", "#33FF57", "#3357FF", "#F333FF", "#FF33A8",
            "#33FFF5", "#F5FF33", "#FF8C33", "#8C33FF", "#33FF8C"
        )
        return colors.random()
    }

    companion object {
        @Volatile
        private var instance: CategoryRepository? = null

        fun getInstance(context: Context): CategoryRepository {
            return instance ?: synchronized(this) {
                instance ?: CategoryRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}