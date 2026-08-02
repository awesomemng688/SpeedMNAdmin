package mn.speed.admin.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.model.ServerStatusResponse
import javax.inject.Inject

@HiltViewModel
class LiveMapViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _status = MutableStateFlow<ServerStatusResponse?>(null)
    val status: StateFlow<ServerStatusResponse?> = _status.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var pollingJob: kotlinx.coroutines.Job? = null

    fun startPolling(ip: String, port: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            _isLoading.value = true
            while (true) {
                try {
                    val response = apiService.getServerLiveStatus(ip, port)
                    if (response.isSuccessful) {
                        _status.value = response.body()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (_isLoading.value) _isLoading.value = false
                }
                delay(10000) // 10 секунд тутамд шинэчилнэ (Ачаалал бууруулав)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
