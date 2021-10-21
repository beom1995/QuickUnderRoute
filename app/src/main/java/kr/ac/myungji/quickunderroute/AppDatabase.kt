/**
 * 앱 내부 데이터베이스 관리 클래스
 * **/

package kr.ac.myungji.quickunderroute

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomStation::class, RoomEdge::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun roomEdgeDao(): RoomEdgeDAO
    abstract fun roomStationDao(): RoomStationDAO
}