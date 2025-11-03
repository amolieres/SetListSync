package com.amolieres.setlistync.feature.main.ui

import com.amolieres.setlistync.feature.main.presentation.MainUiEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amolieres.setlistync.feature.main.presentation.ConfirmDialogType
import com.amolieres.setlistync.feature.main.presentation.MainEvent
import com.amolieres.setlistync.feature.main.presentation.MainUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainUiState,
    uiEventFlow: Flow<MainUiEvent>,
    onScreenEvent: (MainEvent) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiEventFlow) {
        uiEventFlow.collect { event ->
            when (event) {
                is MainUiEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SetListSync") },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Log out") },
                                onClick = {
                                    showMenu = false
                                    onScreenEvent(MainEvent.OnLogoutClicked)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete my account") },
                                onClick = {
                                    showMenu = false
                                    onScreenEvent(MainEvent.OnDeleteAccountClicked)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            // For now empty screen
            Text(
                state.contentText,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    // Dialogs
    state.showConfirmDialog?.let { dialogType ->
        AlertDialog(
            onDismissRequest = { onScreenEvent(MainEvent.OnDialogDismiss) },
            title = { Text(when(dialogType) {
                ConfirmDialogType.Logout -> "Logout"
                ConfirmDialogType.DeleteAccount -> "Delete account"
            }) },
            text = { Text(when(dialogType) {
                ConfirmDialogType.Logout -> "Are you sure you want to log out?"
                ConfirmDialogType.DeleteAccount -> "This will permanently delete your account. Continue?"
            }) },
            confirmButton = {
                TextButton(onClick = {
                    onScreenEvent(when(dialogType) {
                        ConfirmDialogType.Logout -> MainEvent.OnLogoutConfirmed
                        ConfirmDialogType.DeleteAccount -> MainEvent.OnDeleteConfirmed
                    })
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { onScreenEvent(MainEvent.OnDialogDismiss)  }) { Text("Cancel") }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(MainUiState(), emptyFlow(), {}, {})

}