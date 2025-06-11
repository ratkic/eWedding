package com.example.ewedding.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ewedding.model.Guest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.ewedding.model.FirestoreRepository


class GuestListViewModel : ViewModel() {

    private val firestoreRepository = FirestoreRepository()

    private val _guestList = MutableStateFlow<List<Guest>>(emptyList())
    val guestList: StateFlow<List<Guest>> = _guestList

    fun fetchGuests(userId: String) {
        firestoreRepository.getGuests(userId) { guests ->
            _guestList.value = guests
        }
    }

    fun addGuest(userId: String, name: String, isConfirmed: Boolean) {
        firestoreRepository.addGuest(userId, name, isConfirmed)
        fetchGuests(userId)
    }

    fun updateGuest(userId: String, guest: Guest, updatedName: String) {
        firestoreRepository.updateGuest(userId, guest.id, updatedName) {
            fetchGuests(userId)
        }
    }

    fun updateGuestConfirmation(userId: String, guest: Guest, isConfirmed: Boolean) {
        firestoreRepository.updateGuestConfirmation(userId, guest.id, isConfirmed) {
            fetchGuests(userId)
        }
    }

    fun deleteGuest(userId: String, guest: Guest) {
        firestoreRepository.deleteGuest(userId, guest.id) {
            fetchGuests(userId)
        }
    }
}
