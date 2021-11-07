/**
 * 앱 내부 데이터베이스 관리 클래스
 * **/

package kr.ac.myungji.quickunderroute

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kr.ac.myungji.quickunderroute.dao.RoomEdgeDAO
import kr.ac.myungji.quickunderroute.dao.RoomStationDAO
import kr.ac.myungji.quickunderroute.entity.RoomEdge
import kr.ac.myungji.quickunderroute.entity.RoomStation

@Database(entities = [RoomStation::class, RoomEdge::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun roomEdgeDao(): RoomEdgeDAO
    abstract fun roomStationDao(): RoomStationDAO

    companion object {

        val MIGRATION_1_2 = object: Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //
            }
        }

    }
}