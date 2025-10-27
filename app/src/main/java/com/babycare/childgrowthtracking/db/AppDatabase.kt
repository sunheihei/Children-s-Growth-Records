package com.babycare.childgrowthtracking.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.DiaryPhoto
import com.babycare.childgrowthtracking.model.GrowthDiary
import com.babycare.childgrowthtracking.model.GrowthRecord
import com.babycare.childgrowthtracking.model.HeadForAgeBoys
import com.babycare.childgrowthtracking.model.HeadForAgeGirls
import com.babycare.childgrowthtracking.model.HeightForAgeBoys
import com.babycare.childgrowthtracking.model.HeightForAgeGirls
import com.babycare.childgrowthtracking.model.WeightForAgeBoys
import com.babycare.childgrowthtracking.model.WeightForAgeGirls
import com.babycare.childgrowthtracking.utils.DATABASE_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


@Database(
    entities = [Child::class, GrowthRecord::class, GrowthDiary::class, DiaryPhoto::class, HeightForAgeGirls::class, HeightForAgeBoys::class, WeightForAgeGirls::class, WeightForAgeBoys::class, HeadForAgeGirls::class, HeadForAgeBoys::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun childrenDao(): ChildrenDao
    abstract fun growthRecordDao(): GrowthRecordDao
    abstract fun growthDiaryDao(): GrowthDiaryDao
    abstract fun growthStandardDao(): GrowthStandardDao


    companion object {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        DBUtils.initialize(scope, getInstance(context).growthStandardDao())
                    }
                }).fallbackToDestructiveMigration().build()
        }
    }
}
