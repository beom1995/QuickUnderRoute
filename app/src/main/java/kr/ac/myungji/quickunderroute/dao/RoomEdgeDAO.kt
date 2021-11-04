package kr.ac.myungji.quickunderroute.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kr.ac.myungji.quickunderroute.entity.RoomEdge

@Dao
interface RoomEdgeDAO {
    @Query("select * from EDGE_TB")
    fun getAll(): List<RoomEdge>

    @Insert
    fun insert(edge: RoomEdge)

    @Delete
    fun delete(edge: RoomEdge)
}