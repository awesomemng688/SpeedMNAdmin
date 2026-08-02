package mn.speed.admin.data.api

import mn.speed.admin.data.model.ServerItem
import mn.speed.admin.data.model.RankItem
import mn.speed.admin.data.model.NewsItem
import mn.speed.admin.data.model.AuthRequest
import mn.speed.admin.data.model.AuthResponse
import mn.speed.admin.data.model.ServerStatusResponse
import mn.speed.admin.data.model.ClanItem
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/get_servers.php")
    suspend fun getServers(): List<ServerItem>

    @POST("api/app_login.php")
    suspend fun loginUser(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/app_register.php")
    suspend fun registerUser(@Body request: AuthRequest): Response<AuthResponse>

    // Шинэ найдвартай Rank API хаяг
    @GET("api/rank_api.php")
    suspend fun getRanks(@Query("type") type: String): Response<List<RankItem>>

    @GET("api/player_stats.php")
    suspend fun getPlayerProfile(
        @Query("name") playerName: String,
        @Query("type") serverType: String
    ): Response<Map<String, Any>>

    @POST("api/daily_checkin.php")
    suspend fun performDailyCheckIn(
        @Body request: Map<String, String>
    ): Response<Map<String, Any>>

    // Friends System
    @GET("api/get_friends.php")
    suspend fun getFriends(
        @Query("username") username: String
    ): Response<List<Map<String, Any>>>

    @POST("api/friend_actions.php")
    suspend fun performFriendAction(
        @Body request: Map<String, String>
    ): Response<Map<String, Any>>

    // Мэдээ татах
    @GET("api/get_news.php")
    suspend fun getNews(): List<NewsItem>

    // Live Map & Player List
    @GET("api/live_map.php")
    suspend fun getServerLiveStatus(
        @Query("ip") ip: String,
        @Query("port") port: Int
    ): Response<ServerStatusResponse>

    // Logs
    @GET("api/get_logs.php")
    suspend fun getLogs(): Response<List<Map<String, String>>>

    // Клан систем
    @GET("api/clan_api.php")
    suspend fun getClans(): List<ClanItem>

    @GET("api/clan_chat.php")
    suspend fun getClanMessages(
        @Query("clan_id") clanId: String
    ): Response<List<Map<String, Any>>>

    @POST("api/clan_chat.php")
    suspend fun sendClanMessage(
        @Body request: Map<String, String>
    ): Response<Map<String, Any>>

    @POST("api/clan_actions.php")
    suspend fun performClanAction(@Body request: Map<String, String>): Response<Map<String, Any>>

    // Admin Management
    @GET("api/get_admins.php")
    suspend fun getAdmins(): Response<List<Map<String, String>>>

    @POST("api/admin_actions.php")
    suspend fun performAdminAction(@Body request: Map<String, String>): Response<Map<String, Any>>

    @POST("api/rcon_command.php")
    suspend fun sendRconCommand(@Body request: Map<String, String>): Response<Map<String, Any>>

    // Match History
    @GET("api/match_history.php")
    suspend fun getMatchHistory(
        @Query("username") username: String
    ): Response<List<mn.speed.admin.ui.matches.MatchItem>>

    @Multipart
    @POST("api/upload_image.php")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<Map<String, Any>>

    // Сервер удирдах (Start, Stop, Restart)
    @POST("api/server_control.php")
    suspend fun controlServer(@Body request: Map<String, String>): Response<Map<String, Any>>
}
