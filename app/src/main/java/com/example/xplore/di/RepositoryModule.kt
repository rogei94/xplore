package com.example.xplore.di

import com.example.xplore.model.api.DirectionService
import com.example.xplore.model.repository.abstraction.DirectionRepository
import com.example.xplore.model.repository.implementation.DirectionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDirectionRepository(directionService: DirectionService): DirectionRepository {
        return DirectionRepositoryImpl(
            directionService
        )
    }

}