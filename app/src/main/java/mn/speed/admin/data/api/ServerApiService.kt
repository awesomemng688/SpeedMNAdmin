package mn.speed.admin.data.api

import mn.speed.admin.data.model.ServerModel
import retrofit2.http.GET

@Suppress("unused")
interface ServerApiService {
    @GET("get_servers.php")
    suspend fun getServers(): List<ServerModel>
}