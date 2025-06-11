package com.example.ewedding.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.ewedding.model.Expense
import com.example.ewedding.model.FirestoreRepository


class BudgetViewModel : ViewModel() {
    private val firestoreRepository = FirestoreRepository()

    val totalBudget = mutableStateOf(23000.0) // Privremeno dok se ne učita iz Firestore
    val expenses = mutableStateListOf<Expense>()

    fun addExpense(userId: String, expenseName: String, amount: Double) {
        firestoreRepository.addExpense(userId, expenseName, amount) {
            loadExpenses(userId) // Refresh nakon dodavanja
        }
    }

    fun loadExpenses(userId: String) {
        firestoreRepository.getExpenses(userId) { fetchedExpenses ->
            expenses.clear()
            expenses.addAll(fetchedExpenses)
        }
    }

    fun getTotalSpent(): Double = expenses.sumOf { it.amount }

    fun updateTotalBudget(userId: String, newBudget: Double) {
        totalBudget.value = newBudget
        firestoreRepository.updateBudget(userId, newBudget)
    }

    fun loadBudget(userId: String) {
        firestoreRepository.getBudget(userId) { budget ->
            if (budget != null) {
                totalBudget.value = budget
            }
        }
    }

    fun deleteExpense(userId: String, expenseId: String) {
        firestoreRepository.deleteExpense(userId, expenseId) {
            loadExpenses(userId)
        }
    }

}
