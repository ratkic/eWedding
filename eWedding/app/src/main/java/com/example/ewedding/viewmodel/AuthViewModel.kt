package com.example.ewedding.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.ewedding.model.FirestoreRepository
import java.util.*

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestoreRepository = FirestoreRepository()

    // Funkcija za prijavu korisnika
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }

    // Funkcija za registraciju korisnika
    fun register(email: String, password: String, weddingDate: Date?, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null && weddingDate != null) {
                        // Spremanje datuma vjenčanja u Firestore
                        firestoreRepository.setWeddingDate(userId, weddingDate) { success ->
                            if (success) {
                                onResult(true, null)
                            } else {
                                onResult(false, "Failed to save wedding date.")
                            }
                        }
                    } else {
                        onResult(true, null)
                    }
                } else {
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }

    // Funkcija za odjavu korisnika
    fun logout() {
        auth.signOut()
    }

}
