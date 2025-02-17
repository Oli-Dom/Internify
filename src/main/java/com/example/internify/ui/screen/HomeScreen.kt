package com.example.internify.ui.screen


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.internify.model.Internship

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce


@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
){
    var cntr by remember { mutableStateOf(0) }
    var showAddInternshipDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    when (val state = uiState) {
        is HomeScreenUIState.Success -> {
            Scaffold(
                topBar = { CenteredTopAppBar(viewModel) },
                floatingActionButton = {
                    FloatingActionButton(onClick = { showAddInternshipDialog = true }) {
                        Icon(Icons.Filled.Add, "Add")
                    }
                }
            ) { innerPadding ->
                val displayedJobs = if (searchResults.isNotEmpty()) searchResults else state.data
                Column(modifier = Modifier.padding(innerPadding)){
                    if (displayedJobs.isEmpty()) {
                        EmptyListMessage()
                    } else {
                        InternshipListings(
                            jobs = displayedJobs,
                        )
                    }
                }

            }
        }
    }
    if (showAddInternshipDialog) {
        AddInternshipDialog(
            cntr = cntr,
            onDismiss = { showAddInternshipDialog = false },
            onAddInternship = { newInternship ->
                viewModel.addInternship(newInternship)
                showAddInternshipDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInternshipDialog(
    cntr: Int,
    onDismiss: () -> Unit,
    onAddInternship: (Internship) -> Unit
) {
    var companyName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Applied", "Interviewing", "Rejected", "Offer")

    // Check if all fields are filled
    val isFormValid = companyName.isNotBlank() && location.isNotBlank() && status.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Internship") },
        text = {
            Column {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Company Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown Implementation
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = { },
                        label = { Text("Status") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    status = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newInternship = Internship(cntr, companyName, location, status)
                    onAddInternship(newInternship)
                },
                enabled = isFormValid // Disable button if fields are empty
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun EmptyListMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No internships yet! Add one to get started.")
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTopAppBar(viewModel: HomeScreenViewModel){
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = Color.Black
        ),
        title = {
            Text(text = "Internify")
        }
    )
}

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
fun InternshipListings(
    jobs: List<Internship>,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TableHeader()
        LazyColumn {
            items(jobs) { internship ->
                TableRow(internship = internship)
            }
        }
    }
}

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Company",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
        Text(
            text = "Location",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
        Text(
            text = "Status",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun TableRow(internship: Internship) {

    val backgroundColor = when (internship.status) {
        "Applied" -> Color.Gray.copy(alpha = 0.5f)
        "Interviewing" -> Color.Black.copy(alpha = 0.5f)
        "Rejected" -> Color.Red.copy(alpha = 0.5f)
        "Offer" -> Color.Green.copy(alpha = 0.5f)
        else -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = internship.companyName,
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            textAlign = TextAlign.Start
        )
        Text(
            text = internship.location,
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            textAlign = TextAlign.Start
        )
        Text(
            text = internship.status,
            modifier = Modifier
                .weight(1f)
                .background(backgroundColor)
                .padding(4.dp),
            textAlign = TextAlign.Start,

        )
    }
}


