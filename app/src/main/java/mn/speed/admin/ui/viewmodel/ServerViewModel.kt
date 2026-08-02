package mn.speed.admin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.local.AuthManager
import mn.speed.admin.data.model.ServerItem
import mn.speed.admin.data.repository.ServerRepository
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val repository: ServerRepository,
    private val authManager: AuthManager,
    private val apiService: ApiService
) : ViewModel() {

    fun canManageServer(serverId: String): Boolean = authManager.canManageServer(serverId)

    val servers: StateFlow<List<ServerItem>> = repository.servers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPlayers: StateFlow<Int> = servers
        .map { list -> list.sumOf { it.players } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    init {
        // fetchServers() - Асахдаа шууд дуудахыг болиулж, UI бэлэн болсон үед дуудна
    }

    fun fetchServers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.refreshServers()
            } catch (_: Exception) {
                _toastMessage.emit("Алдаа гарлаа")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun controlServer(serverId: String, action: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = mapOf(
                    "serverId" to serverId,
                    "action" to action,
                    "username" to (authManager.getUsername() ?: "")
                )
                val response = apiService.controlServer(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.get("success") == true) {
                        _toastMessage.emit("Үйлдэл ($action) амжилттай боллоо")
                        fetchServers()
                    } else {
                        _toastMessage.emit(body?.get("message")?.toString() ?: "Алдаа гарлаа")
                    }
                } else {
                    _toastMessage.emit("Сервер алдаа: ${response.code()}")
                }
            } catch (e: Exception) {
                _toastMessage.emit("Сүлжээний алдаа: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private var pollingJob: kotlinx.coroutines.Job? = null

    fun startRealtimeUpdates() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                fetchServers()
                delay(10000)
            }
        }
    }

    fun stopRealtimeUpdates() {
        pollingJob?.cancel()
    }
}
