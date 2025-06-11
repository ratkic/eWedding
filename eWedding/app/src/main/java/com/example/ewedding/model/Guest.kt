package com.example.ewedding.model

data class Guest(
    val id: String = "", // Firestore document ID
    val name: String = "",
    var isConfirmed: Boolean = false
)
