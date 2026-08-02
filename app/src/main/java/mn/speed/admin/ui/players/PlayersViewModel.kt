package mn.speed.admin.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.model.PlayerModel
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _players = MutableStateFlow<List<PlayerModel>>(emptyList())
    val players: StateFlow<List<PlayerModel>> = _players.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        refreshPlayers()
    }

    fun refreshPlayers() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                // Бүх серверүүдийг аваад, сервер бүрийн тоглогчдыг нэгтгэх оролдлого
                val servers = apiService.getServers()
                val allPlayers = mutableListOf<PlayerModel>()
                
                servers.forEach { server ->
                    val response = apiService.getServerLiveStatus(server.ip ?: "", server.port)
                    if (response.isSuccessful) {
                        response.body()?.players?.forEachIndexed { index, livePlayer ->
                            allPlayers.add(
                                PlayerModel(
                                    id = "${server.id}_$index",
                                    name = livePlayer.name,
                                    score = livePlayer.score,
                                    time = livePlayer.time,
                                    ping = 0 // API-аас ирдэггүй бол 0
                                )
                            )
                        }
                    }
                }
                _players.value = allPlayers
            } catch (e: Exception) {
                e.printStackTrace()
                // Алдаа гарвал Mock өгөгдөл харуулах (туршилтанд зориулав)
                loadMockPlayers()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun loadMockPlayers() {
        _players.value = listOf(
            PlayerModel("1", "Awesome.!", 15, 24, "45:12"),
            PlayerModel("2", "PlayerTwo", 32, 12, "20:05"),
            PlayerModel("3", "ProGamer_MN", 8, 30, "1:12:40")
        )
    }

    fun kickPlayer(player: PlayerModel) {
        viewModelScope.launch {
            // Ирээдүйд API-аар kick хийх (action=kick, serverId, playerName)
            _players.value = _players.value.filter { it.id != player.id }
        }
    }

    fun banPlayer(player: PlayerModel, reason: String = "Banned", duration: String = "0") {
        viewModelScope.launch {
            // Ирээдүйд API-аар ban хийх
            _players.value = _players.value.filter { it.id != player.id }
        }
    }

    fun mutePlayer(player: PlayerModel) {
        viewModelScope.launch {
            // Mute logic
        }
    }
}