package mn.speed.admin.data.model

data class ServerItem(
    val id: String = "",
    val name: String? = null,
    val ip: String? = null,
    val port: Int = 27015,
    val map: String? = null,
    val players: Int = 0,
    val maxPlayers: Int = 32,
    val isOnline: Boolean = true,
    val rankUrl: String? = null
) {
    val fullAddress: String
        get() = "${ip ?: "203.34.37.57"}:$port"

    val playerRatio: String
        get() = "$players/$maxPlayers"

    val mapImageUrl: String
        get() {
            val mapName = map?.lowercase()?.trim() ?: "de_dust2"
            return "http://speed.mn/assets/map/$mapName.jfif"
        }
}
