package mn.speed.admin.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.local.AuthManager
import javax.inject.Inject

@HiltViewModel
class DailyCheckInViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun checkIn() {
        val username = authManager.getUsername() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.performDailyCheckIn(mapOf("username" to username))
                if (response.isSuccessful) {
                    val msg = response.body()?.get("message")?.toString() ?: "Амжилттай"
                    _toastMessage.emit(msg)
                } else {
                    _toastMessage.emit("Өнөөдөр аль хэдийн Check-in хийсэн байна")
                }
            } catch (e: Exception) {
                _toastMessage.emit("Сүлжээний алдаа")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
