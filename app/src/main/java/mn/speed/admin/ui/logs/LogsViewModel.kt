package mn.speed.admin.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import javax.inject.Inject

data class LogItem(
    val id: String,
    val timestamp: String,
    val tag: String,
    val message: String
)

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _allLogs = MutableStateFlow<List<LogItem>>(emptyList())
    
    private val _selectedFilter = MutableStateFlow("ADMIN")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    val logs: StateFlow<List<LogItem>> = combine(_allLogs, _selectedFilter) { logs, filter ->
        when (filter) {
            "ADMIN" -> logs.filter { it.tag == "RCON" || it.tag == "BAN" || it.tag == "KICK" }
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadLogs()
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun loadLogs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getLogs()
                if (response.isSuccessful) {
                    val rawLogs = response.body() ?: emptyList()
                    _allLogs.value = rawLogs.map { map ->
                        LogItem(
                            id = map["id"] ?: "",
                            timestamp = map["created_at"] ?: "",
                            tag = map["tag"] ?: "SYSTEM",
                            message = map["message"] ?: ""
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearLogs() {
        _allLogs.value = emptyList()
    }
}
