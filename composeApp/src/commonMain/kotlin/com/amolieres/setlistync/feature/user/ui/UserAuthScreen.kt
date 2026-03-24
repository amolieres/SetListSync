package com.amolieres.setlistync.feature.user.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.amolieres.setlistync.feature.user.presentation.UserAuthEvent
import com.amolieres.setlistync.feature.user.presentation.UserAuthUiEvent
import com.amolieres.setlistync.feature.user.presentation.UserAuthUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UserAuthScreen(
    uiState: UserAuthUiState,
    eventFlow: Flow<UserAuthEvent>,
    onNavigateToMain: () -> Unit,
    onScreenEvent: (UserAuthUiEvent) -> Unit,
) {

    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is UserAuthEvent.OnSubmitSuccess -> onNavigateToMain()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (uiState.isLoginMode) "Login" else "Create Account",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))


        AnimatedVisibility(!uiState.isLoginMode) {
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = { onScreenEvent(UserAuthUiEvent.FirstNameChanged(it)) },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }
        AnimatedVisibility(!uiState.isLoginMode) {
            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = { onScreenEvent(UserAuthUiEvent.LastNameChanged(it)) },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { onScreenEvent(UserAuthUiEvent.EmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { onScreenEvent(UserAuthUiEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                onScreenEvent(UserAuthUiEvent.Submit)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isFormValid
        ) {
            Text(if (uiState.isLoginMode) "Login" else "Create Account")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { onScreenEvent(UserAuthUiEvent.ToggleMode) }) {
            Text(
                if (uiState.isLoginMode)
                    "Don't have an account? Create one"
                else
                    "Already have an account? Login"
            )
        }

        if (uiState.isLoading) {
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator()
        }

        uiState.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserAuthScreenPreview() {
    UserAuthScreen(UserAuthUiState(), onScreenEvent = {}, onNavigateToMain = {}, eventFlow = emptyFlow())
}
