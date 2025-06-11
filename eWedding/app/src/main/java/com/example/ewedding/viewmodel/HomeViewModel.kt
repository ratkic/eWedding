package com.example.ewedding.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ewedding.model.FirestoreRepository
import java.util.*

class HomeViewModel : ViewModel() {
    private val firestoreRepository = FirestoreRepository()

    // Funkcija za dohvat datuma vjenčanja iz Firestore-a
    fun getWeddingDate(userId: String?, onResult: (Date?) -> Unit) {
        if (userId != null) {
            firestoreRepository.getWeddingDate(userId) { weddingDate ->
                onResult(weddingDate)
            }
        } else {
            onResult(null)
        }
    }
}
