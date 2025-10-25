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
import com.amolieres.setlistync.feature.user.presentation.UserAuthViewModel

@Composable
fun UserAuthScreen(
    state: UserAuthUiState,
    viewModel: UserAuthViewModel,
    onNavigateToBands: () -> Unit
) {

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UserAuthUiEvent.OnSubmitSuccess -> onNavigateToBands()
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
                onValueChange = { viewModel.onScreenEvent(UserAuthEvent.FirstNameChanged(it)) },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }
        AnimatedVisibility(!state.isLoginMode) {
            OutlinedTextField(
                value = state.lastName,
                onValueChange = { viewModel.onScreenEvent(UserAuthEvent.LastNameChanged(it)) },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onScreenEvent(UserAuthEvent.EmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onScreenEvent(UserAuthEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.onScreenEvent(UserAuthEvent.Submit)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isFormValid
        ) {
            Text(if (state.isLoginMode) "Login" else "Create Account")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = { viewModel.onScreenEvent(UserAuthEvent.ToggleMode) }) {
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
