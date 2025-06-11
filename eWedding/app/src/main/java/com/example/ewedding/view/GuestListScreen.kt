package com.example.ewedding.view


import com.example.ewedding.viewmodel.GuestListViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ewedding.R
import com.example.ewedding.model.Guest
import com.example.ewedding.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ewedding.ui.theme.FontCaslon
import com.example.ewedding.ui.theme.FontMontserrat
import com.example.ewedding.ui.theme.FontPoppins


@Composable
fun GuestListScreen(
    navController: NavController,
    guestListViewModel: GuestListViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val guestList by guestListViewModel.guestList.collectAsState()
    var isDialogOpen by remember { mutableStateOf(false) }
    var guestToEdit by remember { mutableStateOf<Guest?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf("A–Ž") }

    LaunchedEffect(userId) {
        userId?.let { guestListViewModel.fetchGuests(it) }
    }

    val totalGuests = guestList.size
    val confirmedGuests = guestList.count { it.isConfirmed }

    val filteredList = guestList
        .filter { it.name.contains(searchQuery, ignoreCase = true) }
        .sortedWith(compareBy { it.name.lowercase() })

    val sortedList = if (sortOrder == "Ž–A") filteredList.reversed() else filteredList

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())) {

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp)
                    )

                    Button(
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("GuestListScreen") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB28D4F))
                    ) {
                        Text("Logout", color = Color.White, fontFamily = FontPoppins)
                    }
                }

                //Spacer(modifier = Modifier.height(16.dp))

                // Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    IconButton(onClick = { navController.navigate("HomeScreen") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF7C4B00)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Guest list",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 24.sp),
                        color = Color(0xFF7C4B00),
                        fontFamily = FontCaslon
                    )
                }

                //Spacer(modifier = Modifier.height(16.dp))

                // Broj gostiju
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEADBC8)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Total guests:", style = MaterialTheme.typography.bodyMedium, fontFamily = FontMontserrat, color = Color.Black)
                            Text("$totalGuests", style = MaterialTheme.typography.titleLarge, fontFamily = FontMontserrat, color = Color(0xFF7C4B00))
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD5C3A1)),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Confirmed:", style = MaterialTheme.typography.bodyMedium, fontFamily = FontMontserrat, color = Color.Black)
                            Text("$confirmedGuests", style = MaterialTheme.typography.titleLarge, fontFamily = FontMontserrat, color = Color(0xFF7C4B00))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Row: "List" left, "Sort by" right
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
                        color = Color(0xFF7C4B00),
                        fontFamily = FontCaslon
                    )
                    //Sort
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(
                                text = "Sort by",
                                fontFamily = FontMontserrat,
                                color = Color(0xFF999999),
                                style = MaterialTheme.typography.bodyMedium
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

                //Search
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

                Spacer(modifier = Modifier.height(12.dp))

                // Guest list
                Column(
                    modifier = Modifier
                        .padding(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    sortedList.forEach { guest ->
                        GuestCard(
                            guest = guest,
                            onConfirmToggle = {
                                if (userId != null) {
                                    guestListViewModel.updateGuestConfirmation(userId, guest, !guest.isConfirmed)
                                }
                            },
                            onDelete = {
                                if (userId != null) {
                                    guestListViewModel.deleteGuest(userId, guest)
                                }
                            },
                            onEdit = {
                                guestToEdit = guest
                                isDialogOpen = true
                            }
                        )
                    }
                }

            }

            Button(
                onClick = { isDialogOpen = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4B00))
                //Color(0xFFB28D4F)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add guest",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add new guest",
                    color = Color.White,
                    //fontWeight = FontWeight.Bold,
                    fontFamily = FontPoppins
                )
            }

            if (isDialogOpen) {
                GuestDialog(
                    guestList = guestList,
                    initialGuest = guestToEdit,
                    onDismiss = {
                        isDialogOpen = false
                        guestToEdit = null
                    },
                    onSave = { name ->
                        if (userId != null && name.isNotBlank()) {
                            if (guestToEdit == null) {
                                guestListViewModel.addGuest(userId, name, false)
                            } else {
                                guestListViewModel.updateGuest(userId, guestToEdit!!, name)
                            }
                        }
                        isDialogOpen = false
                        guestToEdit = null
                    }
                )
            }


        }
    }
}

@Composable
fun GuestCard(
    guest: Guest,
    onConfirmToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit // funkcionalnost za uređivanje
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBE7))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    guest.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontMontserrat,
                    color = Color(0xFF7C4B00),
                    modifier = Modifier.clickable { onEdit() } // Otvori dijalog kada se ime klikne
                )
                Text(
                    if (guest.isConfirmed) "Confirmed" else "Not confirmed",
                    color = if (guest.isConfirmed) Color(0xFF4CAF50) else Color.Red
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onConfirmToggle) {
                    Icon(
                        painter = painterResource(
                            id = if (guest.isConfirmed) R.drawable.check else R.drawable.circle
                        ),
                        contentDescription = if (guest.isConfirmed) "Confirmed" else "Not confirmed",
                        tint = Color.Unspecified
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete guest", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun GuestDialog(
    guestList: List<Guest>,
    initialGuest: Guest? = null,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var guestName by remember { mutableStateOf(TextFieldValue(initialGuest?.name ?: "")) }
    var showError by remember { mutableStateOf(false) }

    val isEditing = initialGuest != null

    AlertDialog(
        onDismissRequest = {
            onDismiss()
            showError = false
        },
        confirmButton = {
            Button(
                onClick = {
                    val name = guestName.text.trim()

                    val nameExists = guestList.any { guest ->
                        guest.name.equals(name, ignoreCase = true) &&
                                (!isEditing || guest.id != initialGuest?.id)
                    }

                    if (nameExists) {
                        showError = true
                    } else {
                        onSave(name)
                        showError = false
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB28D4F))
            ) {
                Text(
                    text = if (isEditing) "Update" else "Add",
                    color = Color.White,
                    fontFamily = FontPoppins
                )
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
                text = if (isEditing) "Edit guest" else "Add guest",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF7C4B00)
            )
        },
        text = {
            Column {
                Text(
                    text = if (isEditing) "Enter new name for the guest:" else "Enter guest's name:",
                    color = Color(0xFF7C4B00),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = guestName,
                    onValueChange = {
                        guestName = it
                        if (showError) showError = false
                    },
                    placeholder = { Text("Guest name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFFBE7),
                        focusedContainerColor = Color(0xFFFFFBE7),
                        cursorColor = Color(0xFF7C4B00),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFBE7), shape = RoundedCornerShape(12.dp))
                )
                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Guest already exists!",
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
fun GuestListScreenPreview() {
    GuestListScreen(navController = NavController(context = LocalContext.current))
}
