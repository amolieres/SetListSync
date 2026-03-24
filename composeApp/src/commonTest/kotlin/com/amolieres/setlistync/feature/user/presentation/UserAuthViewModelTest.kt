package com.amolieres.setlistync.feature.user.presentation

import com.amolieres.setlistync.core.data.user.FakeUserRepository
import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.usecase.CreateUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.LoginUserUseCase
import com.amolieres.setlistync.feature.BaseViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserAuthViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: UserAuthViewModel
    private lateinit var fakeRepo: FakeUserRepository

   /* @BeforeTest
    fun setup() {
        fakeRepo = FakeUserRepository()
        viewModel = UserAuthViewModel(
            createUserUseCase = CreateUserUseCase(fakeRepo, ),
            loginUserUseCase = LoginUserUseCase(fakeRepo)
        )
    }

    @Test
    fun `should create new user successfully`() = runTest {
        viewModel.onScreenEvent(UserAuthEvent.FirstNameChanged("John"))
        viewModel.onScreenEvent(UserAuthEvent.LastNameChanged("Doe"))
        viewModel.onScreenEvent(UserAuthEvent.EmailChanged("john@doe.com"))
        viewModel.onScreenEvent(UserAuthEvent.PasswordChanged("1234"))
        viewModel.onScreenEvent(UserAuthEvent.ToggleMode) // Passe en création

        val events = mutableListOf<UserAuthUiEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onScreenEvent(UserAuthEvent.Submit)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.currentUser)
        assertTrue(events.any { it is UserAuthUiEvent.OnSubmitSuccess })
        job.cancel()
    }

    @Test
    fun `should show error for duplicate email`() = runTest {
        val existingUser = User("1", "Jane", "Doe", "jane@doe.com", "pass")
        fakeRepo.createUser(existingUser)

        viewModel.onScreenEvent(UserAuthEvent.FirstNameChanged("Jane"))
        viewModel.onScreenEvent(UserAuthEvent.LastNameChanged("Doe"))
        viewModel.onScreenEvent(UserAuthEvent.EmailChanged("jane@doe.com"))
        viewModel.onScreenEvent(UserAuthEvent.PasswordChanged("pass"))
        viewModel.onScreenEvent(UserAuthEvent.ToggleMode)

        viewModel.onScreenEvent(UserAuthEvent.Submit)
        advanceUntilIdle()

        assertEquals("This email is already registered.", viewModel.uiState.value.error)
    }

    @Test
    fun `should show error for invalid email`() = runTest {
        viewModel.onScreenEvent(UserAuthEvent.EmailChanged("invalid-email"))
        viewModel.onScreenEvent(UserAuthEvent.PasswordChanged("pass"))
        viewModel.onScreenEvent(UserAuthEvent.Submit)
        advanceUntilIdle()

        assertEquals("Please enter a valid email address.", viewModel.uiState.value.error)
    }

    @Test
    fun `should emit success when login succeeds`() = runTest {
        val user = User("1", "Tom", "Smith", "tom@smith.com", "secret")
        fakeRepo.createUser(user)

        viewModel.onScreenEvent(UserAuthEvent.EmailChanged("tom@smith.com"))
        viewModel.onScreenEvent(UserAuthEvent.PasswordChanged("secret"))

        val events = mutableListOf<UserAuthUiEvent>()
        val job = launch { viewModel.uiEvent.collect { events.add(it) } }

        viewModel.onScreenEvent(UserAuthEvent.Submit)
        advanceUntilIdle()

        assertTrue(events.any { it is UserAuthUiEvent.OnSubmitSuccess })
        job.cancel()
    }

    @Test
    fun `should show error when password is incorrect`() = runTest {
        val user = User("1", "Tom", "Smith", "tom@smith.com", "secret")
        fakeRepo.createUser(user)

        viewModel.onScreenEvent(UserAuthEvent.EmailChanged("tom@smith.com"))
        viewModel.onScreenEvent(UserAuthEvent.PasswordChanged("wrong"))

        viewModel.onScreenEvent(UserAuthEvent.Submit)
        advanceUntilIdle()

        assertEquals("Incorrect password.", viewModel.uiState.value.error)
    }

    @Test
    fun `should show error when user not found`() = runTest {
        viewModel.onScreenEvent(UserAuthEvent.EmailChanged("unknown@user.com"))
        viewModel.onScreenEvent(UserAuthEvent.PasswordChanged("1234"))

        viewModel.onScreenEvent(UserAuthEvent.Submit)
        advanceUntilIdle()

        assertEquals("User not found. Please create an account.", viewModel.uiState.value.error)
    }*/
}
