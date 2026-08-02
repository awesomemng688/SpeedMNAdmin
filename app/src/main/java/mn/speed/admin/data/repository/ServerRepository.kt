package mn.speed.admin.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mn.speed.admin.data.api.ApiService
import mn.speed.admin.data.local.dao.ServerDao
import mn.speed.admin.data.local.entity.ServerEntity
import mn.speed.admin.data.local.entity.toServerItem
import mn.speed.admin.data.model.ServerItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRepository @Inject constructor(
    private val apiService: ApiService,
    private val serverDao: ServerDao
) {

    // 1. UI хэсэгт харуулах серверийн жагсаалтыг Local Room DB-ээс Real-time (Flow) авна
    val servers: Flow<List<ServerItem>> = serverDao.getAllServers().map { entities ->
        entities.map { it.toServerItem() }
    }

    // 2. Сүлжээнээс серверүүдийн мэдээллийг татаж, Local DB руу шинэчлэн хадгална
    suspend fun refreshServers() {
        try {
            val remoteServers = apiService.getServers()

            // ServerEntity-ийн хүлээж авах төрөлтэй яг тааруулж хөрвүүлэх
            val entityList = remoteServers.map { server ->
                ServerEntity(
                    id = server.id,
                    name = server.name,
                    ip = server.ip,
                    port = server.port,
                    map = server.map,
                    players = server.players,
                    maxPlayers = server.maxPlayers,
                    isOnline = server.isOnline
                )
            }

            serverDao.insertServers(entityList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 3. Сервер рестарт хийх функц
    @Suppress("UNUSED_PARAMETER")
    fun restartServer(port: Int) {
        // Ирээдүйд API эсвэл SSH холболт хийгдэнэ
    }
}