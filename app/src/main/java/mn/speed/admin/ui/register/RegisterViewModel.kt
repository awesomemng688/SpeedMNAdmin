package mn.speed.admin.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.repository.SpeedRepository
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: SpeedRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun register(user: String, email: String, pass: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            val response = repository.register(user, email, pass)
            if (response?.success == true) {
                _registerState.value = RegisterState.Success
            } else {
                _registerState.value = RegisterState.Error(response?.message ?: "Бүртгэл амжилтгүй боллоо.")
            }
        }
    }
}

sealed interface RegisterState {
    object Idle : RegisterState
    object Loading : RegisterState
    object Success : RegisterState
    data class Error(val message: String) : RegisterState
}
