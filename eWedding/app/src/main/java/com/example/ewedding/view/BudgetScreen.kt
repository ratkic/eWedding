package com.example.ewedding.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ewedding.R
import com.example.ewedding.model.Expense
import com.example.ewedding.ui.theme.FontCaslon
import com.example.ewedding.ui.theme.FontMontserrat
import com.example.ewedding.ui.theme.FontPoppins
import com.example.ewedding.viewmodel.BudgetViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat

@Composable
fun BudgetScreen(
    navController: NavController,
    viewModel: BudgetViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val totalBudget = viewModel.totalBudget.value
    val expenses = viewModel.expenses.toList()

    val totalSpent = viewModel.getTotalSpent()
    val remaining = totalBudget - totalSpent
    val progress =
        if (totalBudget > 0) (totalSpent / totalBudget).coerceIn(0.0, 1.0).toFloat() else 0f

    var isAddExpenseDialogOpen by remember { mutableStateOf(false) }
    var isBudgetEditDialogOpen by remember { mutableStateOf(false) }

    val currencyFormatter = remember { NumberFormat.getCurrencyInstance() }

    LaunchedEffect(userId) {
        userId?.let {
            viewModel.loadExpenses(it)
            viewModel.loadBudget(it)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 80.dp)
                    .verticalScroll(rememberScrollState())
            ) {
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
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo("BudgetScreen") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB28D4F))
                    ) {
                        Text("Logout", color = Color.White, fontFamily = FontPoppins)
                    }
                }

                //Spacer(modifier = Modifier.height(16.dp))

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
                    //Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Budget",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 24.sp),
                        fontFamily = FontCaslon,
                        color = Color(0xFF7C4B00)
                    )
                }

                //Spacer(modifier = Modifier.height(8.dp))

                //za uredivanje budgeta
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(IntrinsicSize.Min) // visina po sadržaju
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total budget:",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontMontserrat,
                            color = Color(0xFF7C4B00)
                        )
                        Text(
                            text = currencyFormatter.format(totalBudget),
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontMontserrat,
                            color = Color(0xFF7C4B00)
                        )
                    }

                    IconButton(
                        onClick = { isBudgetEditDialogOpen = true },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = "Edit budget",
                            tint = Color(0xFF999999),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color(0xFFA1E8AF),
                    trackColor = Color(0xFFECECEC)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Spent & Remaining Text Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Spent",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontMontserrat,
                            color = Color.Gray
                        )
                        Text(
                            text = currencyFormatter.format(totalSpent),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontMontserrat,
                            color = Color(0xFF7C4B00)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontMontserrat,
                            color = Color.Gray
                        )
                        Text(
                            text = currencyFormatter.format(remaining),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontMontserrat,
                            color = Color(0xFF7C4B00)
                        )
                    }
                }



                Spacer(modifier = Modifier.height(16.dp))

                // Expense List
                expenses.forEach { expense ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBE7)),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    expense.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontMontserrat,
                                    color = Color(0xFF7C4B00)
                                )
                            }
                            Text(
                                currencyFormatter.format(expense.amount),
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = FontMontserrat,
                                color = Color(0xFF7C4B00)
                            )
                            IconButton(onClick = {
                                userId?.let {
                                    viewModel.deleteExpense(it, expense.id)
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete expense", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { isAddExpenseDialogOpen = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4B00))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add expense",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add new expense",
                    color = Color.White,
                    fontFamily = FontPoppins,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isAddExpenseDialogOpen) {
                val context = LocalContext.current
                AddExpenseDialog(
                    expenses = expenses,
                    onDismiss = { isAddExpenseDialogOpen = false },
                    onConfirm = { name, amount ->
                        if (userId != null && name.isNotBlank() && amount > 0) {
                            viewModel.addExpense(userId, name, amount)

                            // Izračun preostalog budžeta nakon dodavanja troška
                            val newTotalSpent = viewModel.getTotalSpent() + amount
                            val remainingBudget = totalBudget - newTotalSpent

                            val formatter = NumberFormat.getCurrencyInstance()

                            val message = if (remainingBudget >= 0) {
                                "Dodali ste \"$name\" u iznosu od ${formatter.format(amount)}. " +
                                        "Preostalo vam je još ${formatter.format(remainingBudget)}."
                            } else {
                                "Dodali ste \"$name\" u iznosu od ${formatter.format(amount)}. " +
                                        "Budžet je premašen za ${formatter.format(-remainingBudget)}!"
                            }

                            showBudgetNotification(
                                context = context,
                                title = "Nova stavka budžeta",
                                message = message
                            )

                        }
                        isAddExpenseDialogOpen = false
                    }
                )
            }

            if (isBudgetEditDialogOpen) {
                BudgetEditDialog(
                    currentBudget = totalBudget,
                    onDismiss = { isBudgetEditDialogOpen = false },
                    onConfirm = { newAmount ->
                        if (userId != null) {
                            viewModel.updateTotalBudget(userId, newAmount)
                        }
                        isBudgetEditDialogOpen = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    expenses: List<Expense>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showDuplicateError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = name.trim()
                    val amount = amountText.toDoubleOrNull()

                    val alreadyExists = expenses.any {
                        it.name.trim().equals(trimmedName, ignoreCase = true)
                    }

                    when {
                        trimmedName.isEmpty() || amount == null || amount <= 0 -> {
                            showError = true
                            showDuplicateError = false
                        }
                        alreadyExists -> {
                            showDuplicateError = true
                            showError = false
                        }
                        else -> {
                            onConfirm(trimmedName, amount)
                            name = ""
                            amountText = ""
                            showError = false
                            showDuplicateError = false
                            onDismiss()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB28D4F))
            ) {
                Text("Add", color = Color.White, fontFamily = FontPoppins)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                showError = false
                showDuplicateError = false
            }) {
                Text("Cancel", color = Color.Gray, fontFamily = FontPoppins)
            }
        },
        title = {
            Text(
                text = "Add expense",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF7C4B00)
            )
        },
        text = {
            Column {
                Text(
                    "Enter expense details:",
                    color = Color(0xFF7C4B00),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (showError) showError = false
                    },
                    placeholder = { Text("Expense name") },
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
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        if (showError) showError = false
                    },
                    placeholder = { Text("Amount") },
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
                        text = "Please enter valid name and amount greater than zero.",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (showDuplicateError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Expense with this name already exists.",
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

@Composable
fun BudgetEditDialog(
    currentBudget: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var budgetInput by remember { mutableStateOf(currentBudget.toString()) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val parsed = budgetInput.toDoubleOrNull()
                    if (parsed != null && parsed > 0) {
                        onConfirm(parsed)
                    } else {
                        showError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB28D4F))
            ) {
                Text("Save", color = Color.White, fontFamily = FontPoppins)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray, fontFamily = FontPoppins)
            }
        },
        title = {
            Text("Edit budget", style = MaterialTheme.typography.titleLarge, color = Color(0xFF7C4B00))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = {
                        budgetInput = it
                        showError = false
                    },
                    label = { Text("Total budget") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFFFFBE7),
                        focusedContainerColor = Color(0xFFFFFBE7),
                        cursorColor = Color(0xFF7C4B00),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Please enter a valid amount", color = Color.Red)
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

fun showBudgetNotification(context: Context, title: String, message: String) {
    val channelId = "budget_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Budget Notifications"
        val descriptionText = "Notifications for budget updates"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.notification)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val notificationManager = NotificationManagerCompat.from(context)

    if (notificationManager.areNotificationsEnabled()) {
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}

@Preview
@Composable
fun BudgetScreenPreview() {
    BudgetScreen(navController = NavController(context = LocalContext.current))
}



