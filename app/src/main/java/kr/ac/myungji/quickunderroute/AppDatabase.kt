/**
 * 앱 내부 데이터베이스 관리 클래스
 * **/

package kr.ac.myungji.quickunderroute

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RoomStation::class, RoomEdge::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun roomEdgeDao(): RoomEdgeDAO
    abstract fun roomStationDao(): RoomStationDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "subwayInfo.db")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .createFromAsset("subwayInfo.db")
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}