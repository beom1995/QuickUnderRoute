package kr.ac.myungji.quickunderroute.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kr.ac.myungji.quickunderroute.entity.RoomStation

@Dao
interface RoomStationDAO {
    @Query("select * from STATION_TB")
    fun getAll(): List<RoomStation>

    @Query("select `TRANSFER_ST` from STATION_TB where `NO`=:no")
    fun getStationTransSt(no: Int): Int

    @Insert
    fun insert(station: RoomStation)

    @Delete
    fun delete(station: RoomStation)
}