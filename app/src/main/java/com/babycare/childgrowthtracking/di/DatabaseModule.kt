/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.babycare.childgrowthtracking.di

import android.content.Context
import com.babycare.childgrowthtracking.db.AppDatabase
import com.babycare.childgrowthtracking.db.ChildrenDao
import com.babycare.childgrowthtracking.db.GrowthDiaryDao
import com.babycare.childgrowthtracking.db.GrowthRecordDao
import com.babycare.childgrowthtracking.db.GrowthStandardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }


    @Provides
    fun provideChildrenDao(appDatabase: AppDatabase): ChildrenDao {
        return appDatabase.childrenDao()
    }

    @Provides
    fun provideGrowthRecordDao(appDatabase: AppDatabase): GrowthRecordDao {
        return appDatabase.growthRecordDao()
    }

    @Provides
    fun provideGrowthDiaryDao(appDatabase: AppDatabase): GrowthDiaryDao {
        return appDatabase.growthDiaryDao()
    }

    @Provides
    @Singleton
    fun provideGrowthStandardDao(database: AppDatabase): GrowthStandardDao {
        return database.growthStandardDao()
    }

}
