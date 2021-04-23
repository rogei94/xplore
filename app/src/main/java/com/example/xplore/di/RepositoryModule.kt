package com.example.xplore.di

import com.example.xplore.model.api.GoogleService
import com.example.xplore.model.repository.abstraction.DirectionRepository
import com.example.xplore.model.repository.abstraction.GeocodeRepository
import com.example.xplore.model.repository.implementation.DirectionRepositoryImpl
import com.example.xplore.model.repository.implementation.GeocodeRepositoryImpl
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
    fun provideDirectionRepository(googleService: GoogleService): DirectionRepository {
        return DirectionRepositoryImpl(
            googleService
        )
    }

    @Singleton
    @Provides
    fun provideGeocodenRepository(googleService: GoogleService): GeocodeRepository {
        return GeocodeRepositoryImpl(
            googleService
        )
    }

}