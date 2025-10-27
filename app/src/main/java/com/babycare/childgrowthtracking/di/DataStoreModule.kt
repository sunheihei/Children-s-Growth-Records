package com.babycare.childgrowthtracking.di

import android.content.Context
import com.babycare.childgrowthtracking.datastore.GrowthTrackerDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object DataStoreModule {
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): GrowthTrackerDataStore {
        return GrowthTrackerDataStore(context)
    }
}