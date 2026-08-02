package mn.speed.admin.ui.check

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.model.ServerItem
import javax.inject.Inject

data class ServerStatus(
    val server: ServerItem,
    val isOnline: Boolean = false,
    val players: String = "0/0",
    val map: String = "N/A",
    val ping: Long = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class ServerCheckViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _serverStatuses = MutableStateFlow<List<ServerStatus>>(emptyList())
    val serverStatuses: StateFlow<List<ServerStatus>> = _serverStatuses.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadAndCheckServers()
    }

    fun loadAndCheckServers() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val servers = apiService.getServers()
                _serverStatuses.value = servers.map { ServerStatus(it, isLoading = true) }
                
                // Check each server individually
                servers.forEachIndexed { index, server ->
                    checkIndividualServer(index, server)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun checkIndividualServer(index: Int, server: ServerItem) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            try {
                val response = apiService.getServerLiveStatus(server.ip ?: "", server.port)
                val endTime = System.currentTimeMillis()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val info = response.body()?.info
                    updateStatus(index) {
                        it.copy(
                            isOnline = true,
                            players = "${info?.players}/${info?.maxPlayers}",
                            map = info?.map ?: "Unknown",
                            ping = endTime - startTime,
                            isLoading = false
                        )
                    }
                } else {
                    updateStatus(index) { it.copy(isOnline = false, isLoading = false) }
                }
            } catch (e: Exception) {
                updateStatus(index) { it.copy(isOnline = false, isLoading = false) }
            }
        }
    }

    private fun updateStatus(index: Int, transformer: (ServerStatus) -> ServerStatus) {
        val currentList = _serverStatuses.value.toMutableList()
        if (index < currentList.size) {
            currentList[index] = transformer(currentList[index])
            _serverStatuses.value = currentList
        }
    }
}
