package mn.speed.admin.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.speed.admin.data.api.ApiService
import javax.inject.Inject

data class PlayerStats(
    val kills: Int = 0,
    val deaths: Int = 0,
    val assists: Int = 0,
    val headshots: Int = 0,
    val mvp: Int = 0,
    val firstBlood: Int = 0,
    val winRate: Int = 0,
    val accuracy: Int = 0,
    val rankPoints: Int = 0,
    val globalRank: Int = 0,
    val steamId: String = "",
    val joinedDate: String = "",
    val lastPlayed: String = "",
    val onlineTime: Int = 0,
    val favoriteWeapon: String = "AK-47",
    val favoriteMap: String = "de_dust2",
    val skill: String = "Pro",
    val damageZones: Map<String, Int> = emptyMap()
)

@HiltViewModel
class PlayerProfileViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _stats = MutableStateFlow<PlayerStats?>(null)
    val stats: StateFlow<PlayerStats?> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchPlayerProfile(playerName: String, serverType: String = "pub1") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPlayerProfile(playerName, serverType)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    _stats.value = PlayerStats(
                        kills = (body?.get("kills") as? Double)?.toInt() ?: 0,
                        deaths = (body?.get("deaths") as? Double)?.toInt() ?: 0,
                        assists = (body?.get("assists") as? Double)?.toInt() ?: 0,
                        headshots = (body?.get("headshots") as? Double)?.toInt() ?: 0,
                        mvp = (body?.get("mvp") as? Double)?.toInt() ?: 0,
                        firstBlood = (body?.get("first_blood") as? Double)?.toInt() ?: 0,
                        winRate = (body?.get("win_rate") as? Double)?.toInt() ?: 0,
                        accuracy = (body?.get("accuracy") as? Double)?.toInt() ?: 0,
                        rankPoints = (body?.get("rank_points") as? Double)?.toInt() ?: 0,
                        globalRank = (body?.get("global_rank") as? Double)?.toInt() ?: 0,
                        steamId = body?.get("steamid")?.toString() ?: "",
                        joinedDate = body?.get("joined_date")?.toString() ?: "",
                        lastPlayed = body?.get("last_played")?.toString() ?: "",
                        onlineTime = (body?.get("online_time") as? Double)?.toInt() ?: 0,
                        favoriteWeapon = body?.get("fav_weapon")?.toString() ?: "AK-47",
                        favoriteMap = body?.get("fav_map")?.toString() ?: "de_dust2",
                        skill = body?.get("skill")?.toString() ?: "Pro",
                        damageZones = (body?.get("damage_zones") as? Map<String, Double>)?.mapValues { it.value.toInt() } ?: emptyMap()
                    )
                } else {
                    // Серверээс алдаа ирвэл Demo өгөгдөл харуулах
                    loadDemoData(playerName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                loadDemoData(playerName)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadDemoData(playerName: String) {
        _stats.value = PlayerStats(
            kills = 5700,
            deaths = 4100,
            assists = 1100,
            headshots = 3000,
            mvp = 1593,
            firstBlood = 1021,
            winRate = 57,
            accuracy = 22,
            rankPoints = 164583,
            globalRank = 4,
            steamId = "STEAM_1:0:486672651",
            joinedDate = "Nov 2, 2025",
            lastPlayed = "8m ago",
            onlineTime = 12500,
            favoriteWeapon = "AK-47",
            favoriteMap = "de_dust2",
            skill = "Global Elite",
            damageZones = mapOf(
                "Head" to 2997,
                "Neck" to 44,
                "Chest" to 4279,
                "Stomach" to 1569,
                "Arms" to 1067,
                "Legs" to 464
            )
        )
    }
}
