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
    state: UserAuthUiState,
    uiEventFlow: Flow<UserAuthUiEvent>,
    onNavigateToMain: () -> Unit,
    onScreenEvent: (UserAuthEvent) -> Unit,
) {

    LaunchedEffect(uiEventFlow) {
        uiEventFlow.collect { event ->
            when (event) {
                is UserAuthUiEvent.OnSubmitSuccess -> onNavigateToMain()
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
            text = if (state.isLoginMode) "Login" else "Create Account",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))


        AnimatedVisibility(!state.isLoginMode) {
            OutlinedTextField(
                value = state.firstName,
                onValueChange = { onScreenEvent(UserAuthEvent.FirstNameChanged(it)) },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }
        AnimatedVisibility(!state.isLoginMode) {
            OutlinedTextField(
                value = state.lastName,
                onValueChange = { onScreenEvent(UserAuthEvent.LastNameChanged(it)) },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = state.email,
            onValueChange = { onScreenEvent(UserAuthEvent.EmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { onScreenEvent(UserAuthEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                onScreenEvent(UserAuthEvent.Submit)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isFormValid
        ) {
            Text(if (state.isLoginMode) "Login" else "Create Account")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { onScreenEvent(UserAuthEvent.ToggleMode) }) {
            Text(
                if (state.isLoginMode)
                    "Don't have an account? Create one"
                else
                    "Already have an account? Login"
            )
        }

        if (state.isLoading) {
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator()
        }

        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserAuthScreenPreview() {
    UserAuthScreen(UserAuthUiState(), onScreenEvent = {}, onNavigateToMain = {}, uiEventFlow = emptyFlow())
}
