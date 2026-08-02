package mn.speed.admin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import mn.speed.admin.data.model.ServerItem

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey val port: Int,
    val id: String,
    val name: String?,
    val ip: String?,
    val map: String?,
    val players: Int,
    val maxPlayers: Int,
    val isOnline: Boolean
)

// Entity-ийг UI дээр ашиглах ServerItem руу хөрвүүлэх
@Suppress("unused")
fun ServerEntity.toServerItem() = ServerItem(
    id = id,
    name = name,
    ip = ip,
    port = port,
    map = map,
    players = players,
    maxPlayers = maxPlayers,
    isOnline = isOnline
)

// UI-ийн ServerItem-ийг Room бааз руу хадгалах Entity руу хөрвүүлэх
@Suppress("unused")
fun ServerItem.toEntity() = ServerEntity(
    id = id,
    name = name,
    ip = ip,
    port = port,
    map = map,
    players = players,
    maxPlayers = maxPlayers,
    isOnline = isOnline
)