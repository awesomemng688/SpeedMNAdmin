package mn.speed.admin.data.model

import com.google.gson.annotations.SerializedName

data class RankItem(
    @SerializedName("rank") val rank: Int = 0,
    @SerializedName("playerName") val playerName: String? = "Player",
    @SerializedName("kills") val kills: Int = 0,
    @SerializedName("deaths") val deaths: Int = 0,
    @SerializedName("xp") val xp: Int = 0,
    @SerializedName("skill") val skill: String? = "SILVER I",
    @SerializedName("kdRatio") val kdRatio: Double = 0.0,
    @SerializedName("hs_percent") val hsPercent: String? = "0.0%",
    @SerializedName("played_time") val playedTime: String? = "0ц",
    @SerializedName("avatar") val avatarUrl: String? = null
)