package mn.speed.admin.data.model

data class ServerModel(
    val id: String,
    val name: String,
    val ip: String,
    val port: Int,
    val currentPlayers: Int,
    val maxPlayers: Int,
    val map: String,
    val status: Boolean // Online / Offline
)