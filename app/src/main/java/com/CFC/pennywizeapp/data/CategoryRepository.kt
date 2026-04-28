package com.CFC.pennywizeapp.data

import android.content.Context
import com.CFC.pennywizeapp.data.local.DatabaseProvider
import com.CFC.pennywizeapp.models.Category
import com.CFC.pennywizeapp.models.CategoryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Category Repository - Manages all category data operations
 * BACKED BY ROOM DATABASE
 */
object CategoryRepository {

    private lateinit var context: Context
    private var isInitialized = false

    private var _categories: Flow<List<Category>> = flow { emit(emptyList()) }

    val categories: Flow<List<Category>>
        get() = _categories

    fun init(appContext: Context) {
        if (!isInitialized) {
            context = appContext.applicationContext
            val database = DatabaseProvider.getDatabase(context)
            val categoryDao = database.categoryDao()

            _categories = categoryDao.getAllCategories()
                .catch { e -> e.printStackTrace() }
                .stateIn(
                    scope = CoroutineScope(Dispatchers.IO),
                    started = SharingStarted.Eagerly,
                    initialValue = emptyList()
                )
            isInitialized = true

            CoroutineScope(Dispatchers.IO).launch {
                prePopulateCategories()
            }
        }
    }

    fun addCategory(category: Category) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = DatabaseProvider.getDatabase(context)
            database.categoryDao().insertCategory(category)
        }
    }

    fun addCategories(categories: List<Category>) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = DatabaseProvider.getDatabase(context)
            database.categoryDao().insertCategories(categories)
        }
    }

    private suspend fun prePopulateCategories() {
        val database = DatabaseProvider.getDatabase(context)
        val categoryDao = database.categoryDao()

        val existingCategories = categoryDao.getAllCategories().first()
        if (existingCategories.isNotEmpty()) return

        val defaultCategories = listOf(
            // Expense categories
            Category(id = "1", name = "Groceries", type = CategoryType.EXPENSE, color = "#FF5733", icon = "shopping_cart"),
            Category(id = "2", name = "Transport", type = CategoryType.EXPENSE, color = "#33FF57", icon = "directions_bus"),
            Category(id = "3", name = "Shopping", type = CategoryType.EXPENSE, color = "#E91E63", icon = "shopping_bag"),
            Category(id = "4", name = "Bills", type = CategoryType.EXPENSE, color = "#F44336", icon = "receipt"),
            Category(id = "5", name = "Entertainment", type = CategoryType.EXPENSE, color = "#9C27B0", icon = "movie"),
            Category(id = "6", name = "Health", type = CategoryType.EXPENSE, color = "#00BCD4", icon = "favorite"),
            Category(id = "7", name = "Dining Out", type = CategoryType.EXPENSE, color = "#FF5722", icon = "restaurant"),
            Category(id = "8", name = "Other", type = CategoryType.EXPENSE, color = "#9E9E9E", icon = "help"),

            // Income categories
            Category(id = "9", name = "Salary", type = CategoryType.INCOME, color = "#3357FF", icon = "attach_money"),
            Category(id = "10", name = "Freelance", type = CategoryType.INCOME, color = "#2196F3", icon = "computer"),
            Category(id = "11", name = "Gift", type = CategoryType.INCOME, color = "#FFC107", icon = "card_giftcard"),
            Category(id = "12", name = "Investment", type = CategoryType.INCOME, color = "#009688", icon = "trending_up"),
            Category(id = "13", name = "Other Income", type = CategoryType.INCOME, color = "#9E9E9E", icon = "attach_money")
        )

        categoryDao.insertCategories(defaultCategories)
        println("CategoryRepository: Added ${defaultCategories.size} default categories")
    }
}