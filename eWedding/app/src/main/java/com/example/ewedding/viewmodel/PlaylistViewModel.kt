package com.example.ewedding.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ewedding.model.FirestoreRepository
import com.example.ewedding.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlaylistViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist

    fun fetchSongs(userId: String) {
        repository.fetchSongs(userId) { songs ->
            _playlist.value = songs
        }
    }

    fun addSong(userId: String, title: String, artist: String) {
        repository.addSong(userId, title, artist) {
            fetchSongs(userId)
        }
    }

    fun updateSong(userId: String, song: Song) {
        repository.updateSong(userId, song) {
            fetchSongs(userId)
        }
    }

    fun deleteSong(userId: String, song: Song) {
        repository.deleteSong(userId, song.id) {
            fetchSongs(userId)
        }
    }
}
