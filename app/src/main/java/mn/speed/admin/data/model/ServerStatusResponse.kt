package mn.speed.admin.data.model

data class ServerStatusResponse(
    val success: Boolean,
    val info: ServerLiveInfo,
    val players: List<LivePlayer>
)

data class ServerLiveInfo(
    val status: String,
    val name: String,
    val map: String,
    val mapImageUrl: String,
    val players: Int,
    val maxPlayers: Int
)

data class LivePlayer(
    val name: String,
    val score: Int,
    val time: String
)
