package mn.speed.admin.ui.rcon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.local.AuthManager
import javax.inject.Inject

@HiltViewModel
class RconViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authManager: AuthManager
) : ViewModel() {

    private val _consoleLogs = MutableStateFlow<List<String>>(emptyList())
    val consoleLogs: StateFlow<List<String>> = _consoleLogs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendCommand(serverId: String, command: String) {
        if (command.isBlank()) return
        
        val username = authManager.getUsername() ?: "System"
        
        viewModelScope.launch {
            _consoleLogs.value += "> $command"
            _isLoading.value = true
            try {
                val request = mapOf(
                    "serverId" to serverId,
                    "command" to command,
                    "username" to username
                )
                val response = apiService.sendRconCommand(request)
                if (response.isSuccessful) {
                    val result = response.body()?.get("output")?.toString() ?: "No output from server"
                    _consoleLogs.value += result
                } else {
                    _consoleLogs.value += "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _consoleLogs.value += "Network Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearLogs() {
        _consoleLogs.value = emptyList()
    }
}
