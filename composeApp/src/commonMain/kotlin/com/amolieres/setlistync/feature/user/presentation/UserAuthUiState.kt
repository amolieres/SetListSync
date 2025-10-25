package com.amolieres.setlistync.feature.user.presentation

import com.amolieres.setlistync.core.domain.user.model.User

data class UserAuthUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: User? = null
) {
    val isFormValid: Boolean
        get() = email.isNotBlank() && password.isNotBlank() &&
                (isLoginMode || (firstName.isNotBlank() && lastName.isNotBlank()))
}