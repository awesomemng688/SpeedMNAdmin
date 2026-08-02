package mn.speed.admin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import mn.speed.admin.data.local.dao.ServerDao
import mn.speed.admin.data.local.entity.ServerEntity

@Database(entities = [ServerEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun serverDao(): ServerDao
}