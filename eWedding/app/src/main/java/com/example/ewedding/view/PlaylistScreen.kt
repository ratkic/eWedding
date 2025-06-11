package com.example.ewedding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ewedding.R
import com.example.ewedding.model.Song
import com.example.ewedding.ui.theme.FontCaslon
import com.example.ewedding.ui.theme.FontMontserrat
import com.example.ewedding.ui.theme.FontPoppins
import com.example.ewedding.viewmodel.AuthViewModel
import com.example.ewedding.viewmodel.PlaylistViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PlaylistScreen(
    navController: NavController,
    playlistViewModel: PlaylistViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val songs by playlistViewModel.playlist.collectAsState()
    var isDialogOpen by remember { mutableStateOf(false) }
    var songToEdit by remember { mutableStateOf<Song?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf("A–Ž") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        userId?.let { playlistViewModel.fetchSongs(it) }
    }

    val filtered = songs.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.artist.contains(searchQuery, ignoreCase = true)
    }.sortedWith(compareBy { it.title.lowercase() })

    val sortedList = if (sortOrder == "Ž–A") filtered.reversed() else filtered

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())  // dodaje scroll
            ) {
                // Header: logo + logout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(80.dp)
                    )
                    Button(
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("PlaylistScreen") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB28D4F))
                    ) {
                        Text("Logout", color = Color.White, fontFamily = FontPoppins)
                    }
                }

                //Spacer(modifier = Modifier.height(16.dp))

                // Naslov + gumb za povratak (HomeScreen)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.navigate("HomeScreen") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF7C4B00)
                        )
                    }
                    Text(
                        text = "Playlist",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontCaslon,
                        color = Color(0xFF7C4B00)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Total songs
                Text(
                    text = "Total songs: ${songs.size}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontMontserrat,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                var expanded by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "List",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        fontFamily = FontCaslon,
                        color = Color(0xFF7C4B00)
                    )
                    // Sort
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(
                                text = "Sort by",
                                color = Color(0xFF999999),
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontMontserrat
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Sort icon",
                                tint = Color(0xFF999999)
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("A–Ž") },
                                onClick = {
                                    sortOrder = "A–Ž"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Ž–A") },
                                onClick = {
                                    sortOrder = "Ž–A"
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Search", color = Color.Gray, fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(50),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.LightGray,
                        unfocusedBorderColor = Color.LightGray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.DarkGray
                    )
                )


                Spacer(modifier = Modifier.height(16.dp))

                // Lista pjesama
                Column(
                    modifier = Modifier
                        .padding(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sortedList.forEach { song ->
                        SongCard(
                            song = song,
                            onEdit = {
                                songToEdit = song
                                isDialogOpen = true
                            },
                            onDelete = {
                                userId?.let { playlistViewModel.deleteSong(it, song) }
                            }
                        )
                    }
                }

            }

                // Button "Add new song"
                Button(
                    onClick = {
                        songToEdit = null
                        isDialogOpen = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4B00))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add song",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add new song", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontPoppins)
                }
        }
    }

    if (isDialogOpen) {
        SongDialog(
            songList = songs,
            initialSong = songToEdit,
            onDismiss = { isDialogOpen = false },
            onSave = { title, artist ->
                userId?.let {
                    if (songToEdit == null)
                        playlistViewModel.addSong(it, title, artist)
                    else
                        playlistViewModel.updateSong(it, songToEdit!!.copy(title = title, artist = artist))
                }
                isDialogOpen = false
            }
        )
    }

}


@Composable
private fun SongCard(song: Song, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBE7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontMontserrat,
                    color = Color(0xFF7C4B00),
                    modifier = Modifier.clickable { onEdit() }
                )
                Text(
                    text = song.artist,
                    fontSize = 14.sp,
                    fontFamily = FontMontserrat,
                    color = Color.Gray
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit song", tint = Color(0xFF7C4B00))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete song", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun SongDialog(
    songList: List<Song>,
    initialSong: Song? = null,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialSong?.title ?: "") }
    var artist by remember { mutableStateOf(initialSong?.artist ?: "") }
    var showError by remember { mutableStateOf(false) }
    var duplicateError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val trimmedTitle = title.trim()
                    val trimmedArtist = artist.trim()

                    val exists = songList.any { song ->
                        song.title.equals(trimmedTitle, ignoreCase = true) &&
                                song.artist.equals(trimmedArtist, ignoreCase = true) &&
                                (song.id != initialSong?.id) // ako se uređuje, ne uspoređuj sa samim sobom
                    }

                    if (trimmedTitle.isEmpty() || trimmedArtist.isEmpty()) {
                        showError = true
                        duplicateError = false
                    } else if (exists) {
                        duplicateError = true
                        showError = false
                    } else {
                        onSave(trimmedTitle, trimmedArtist)
                        showError = false
                        duplicateError = false
                        onDismiss()
                    }


                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB28D4F))
            ) {
                Text(if (initialSong == null) "Add" else "Update", color = Color.White, fontFamily = FontPoppins)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                showError = false
            }) {
                Text("Cancel", color = Color.Gray, fontFamily = FontPoppins)
            }
        },
        title = {
            Text(
                text = if (initialSong == null) "Add song" else "Edit song",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF7C4B00)
            )
        },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (showError) showError = false
                    },
                    label = { Text("Title") },
                    placeholder = { Text("Song title") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFFBE7),
                        focusedContainerColor = Color(0xFFFFFBE7),
                        cursorColor = Color(0xFF7C4B00),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = Color(0xFF7C4B00),
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFBE7), shape = RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = artist,
                    onValueChange = {
                        artist = it
                        if (showError) showError = false
                    },
                    label = { Text("Artist") },
                    placeholder = { Text("Song artist") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFFBE7),
                        focusedContainerColor = Color(0xFFFFFBE7),
                        cursorColor = Color(0xFF7C4B00),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedLabelColor = Color(0xFF7C4B00),
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFBE7), shape = RoundedCornerShape(12.dp))
                )
                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Both title and artist must be filled!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (duplicateError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This song already exists!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@Preview
@Composable
fun PlaylistScreenPreview() {
    PlaylistScreen(navController = NavController(context = LocalContext.current))
}
