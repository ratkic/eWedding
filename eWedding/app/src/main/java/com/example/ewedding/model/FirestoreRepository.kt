package com.example.ewedding.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // Spremanje datuma vjenčanja
    fun setWeddingDate(userId: String, weddingDate: Date, onResult: (Boolean) -> Unit) {
        val weddingData = mapOf("weddingDateMillis" to weddingDate.time)
        db.collection("users").document(userId)
            .set(weddingData)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    // Dohvaćanje datuma vjenčanja
    fun getWeddingDate(userId: String, onResult: (Date?) -> Unit) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val millis = document.getLong("weddingDateMillis")
                if (millis != null) {
                    onResult(Date(millis))
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error getting wedding date", it)
                onResult(null)
            }
    }


    //********GUEST LIST SCREEN**********
    // Spremanje podataka o gostu
    fun getGuests(userId: String, onResult: (List<Guest>) -> Unit) {
        db.collection("users").document(userId).collection("guests")
            .get()
            .addOnSuccessListener { snapshot ->
                val guests = snapshot.documents.mapNotNull {
                    val name = it.getString("name")
                    val isConfirmed = it.getBoolean("isConfirmed") ?: false
                    val id = it.id
                    if (name != null) Guest(id, name, isConfirmed) else null
                }
                onResult(guests)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error getting guests", it)
                onResult(emptyList())
            }
    }

    fun addGuest(userId: String, guestName: String, isConfirmed: Boolean, onComplete: () -> Unit = {}) {
        val guestData = mapOf(
            "name" to guestName,
            "isConfirmed" to isConfirmed
        )
        db.collection("users").document(userId).collection("guests")
            .add(guestData)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error adding guest", exception)
            }
    }


    fun updateGuest(userId: String, guestId: String, newName: String, onComplete: () -> Unit = {}) {
        db.collection("users").document(userId)
            .collection("guests").document(guestId)
            .update("name", newName)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener {
                Log.e("Firestore", "Error updating guest", it)
            }
    }

    //potvrda dolaska
    fun updateGuestConfirmation(userId: String, guestId: String, isConfirmed: Boolean, onComplete: () -> Unit = {}) {
        db.collection("users").document(userId)
            .collection("guests").document(guestId)
            .update("isConfirmed", isConfirmed)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener {
                Log.e("Firestore", "Error updating confirmation", it)
            }
    }

    fun deleteGuest(userId: String, guestId: String, onComplete: () -> Unit = {}) {
        db.collection("users").document(userId)
            .collection("guests").document(guestId)
            .delete()
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener {
                Log.e("Firestore", "Error deleting guest", it)
            }
    }


    //********BUDGET SCREEN**********
    // Spremanje troška
    fun addExpense(userId: String, name: String, amount: Double, onComplete: () -> Unit) {
        val expense = mapOf("name" to name, "amount" to amount)
         db.collection("users").document(userId)
            .collection("expenses")
            .add(expense)
            .addOnSuccessListener { onComplete() }
    }

    fun getExpenses(userId: String, onResult: (List<Expense>) -> Unit) {
        db.collection("users").document(userId)
            .collection("expenses")
            .get()
            .addOnSuccessListener { snapshot ->
                val expenses = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    //val description = doc.getString("description")
                    val amount = doc.getDouble("amount") ?: return@mapNotNull null
                    //Expense(id = doc.id, name = name, description = description, amount = amount)
                    Expense(id = doc.id, name = name, amount = amount)
                }
                onResult(expenses)
            }
    }


    fun updateBudget(userId: String, newBudget: Double, onComplete: () -> Unit = {}) {
        val data = mapOf("totalBudget" to newBudget)
        db.collection("users").document(userId)
            .set(data, com.google.firebase.firestore.SetOptions.merge()) //merge - ne brise ostale podatke
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to update budget", it)
            }
    }

    fun deleteExpense(userId: String, expenseId: String, onComplete: () -> Unit = {}) {
        db.collection("users").document(userId)
            .collection("expenses").document(expenseId)
            .delete()
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to delete expense", it)
            }
    }


    //dohvaćanje budžeta
    fun getBudget(userId: String, onResult: (Double?) -> Unit) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val budget = document.getDouble("totalBudget")
                onResult(budget)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to load budget", it)
                onResult(null)
            }
    }


    //********PLAYLIST SCREEN**********
    // Dodaj pjesmu
    fun addSong(userId: String, title: String, artist: String, onResult: () -> Unit = {}) {
        val song = mapOf("title" to title, "artist" to artist)
        db.collection("users").document(userId)
            .collection("songs")
            .add(song)
            .addOnSuccessListener { onResult() }
    }

    // Dohvati sve pjesme
    fun fetchSongs(userId: String, onResult: (List<Song>) -> Unit) {
        db.collection("users").document(userId)
            .collection("songs")
            .get()
            .addOnSuccessListener { snapshot ->
                val songs = snapshot.documents.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val artist = doc.getString("artist") ?: return@mapNotNull null
                    Song(id = doc.id, title = title, artist = artist)
                }
                onResult(songs)
            }
    }

    // Ažuriraj pjesmu
    fun updateSong(userId: String, song: Song, onResult: () -> Unit = {}) {
        val updated = mapOf("title" to song.title, "artist" to song.artist)
        db.collection("users").document(userId)
            .collection("songs").document(song.id)
            .set(updated)
            .addOnSuccessListener { onResult() }
    }

    // Obriši pjesmu
    fun deleteSong(userId: String, songId: String, onResult: () -> Unit = {}) {
        db.collection("users").document(userId)
            .collection("songs").document(songId)
            .delete()
            .addOnSuccessListener { onResult() }
    }


}
