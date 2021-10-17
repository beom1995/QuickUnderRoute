package kr.ac.myungji.quickunderroute

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface RoomStationDAO {
    @Query("select * from STATION_TB")
    fun getAll(): List<RoomStation>

    @Insert(onConflict = REPLACE)
    fun insert(station: RoomStation)

    @Delete
    fun delete(station: RoomStation)
}