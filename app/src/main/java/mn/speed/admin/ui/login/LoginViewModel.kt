package mn.speed.admin.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.local.AuthManager
import mn.speed.admin.data.repository.SpeedRepository
import mn.speed.admin.data.repository.SpeedRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: SpeedRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(user: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            // SpeedRepository-г SpeedRepositoryImpl руу хөрвүүлж бодит хариуг авна
            val result = (repository as? SpeedRepositoryImpl)?.loginWithFullResponse(user, pass)
            
            if (result?.success == true) {
                repository.logAction("Admin user $user logged in successfully.")
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Error(result?.message ?: "Нэвтрэх нэр эсвэл нууц үг буруу.")
            }
        }
    }

    fun loginAsGuest() {
        authManager.setGuestSession()
        _loginState.value = LoginState.Success
    }
}

sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    object Success : LoginState
    data class Error(val message: String) : LoginState
}
