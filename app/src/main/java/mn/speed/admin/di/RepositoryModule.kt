package mn.speed.admin.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mn.speed.admin.data.repository.SpeedRepository
import mn.speed.admin.data.repository.SpeedRepositoryImpl
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSpeedRepository(
        speedRepositoryImpl: SpeedRepositoryImpl
    ): SpeedRepository
}