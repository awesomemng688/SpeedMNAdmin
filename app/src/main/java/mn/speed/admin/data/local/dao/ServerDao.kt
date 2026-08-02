package mn.speed.admin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mn.speed.admin.data.local.entity.ServerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {

    // Local Database-д байгаа бүх серверүүдийг Real-time (Flow) байдлаар унших
    @Query("SELECT * FROM servers")
    fun getAllServers(): Flow<List<ServerEntity>>

    // Серверийн жагсаалтыг шинэчлэн хадгалах (Давхардвал шууд сольно)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<ServerEntity>)

    // Кэшлэгдсэн бүх серверийн мэдээллийг устгах
    @Query("DELETE FROM servers")
    suspend fun clearAll()
}