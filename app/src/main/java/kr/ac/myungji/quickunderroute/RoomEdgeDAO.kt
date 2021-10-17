package kr.ac.myungji.quickunderroute

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface RoomEdgeDAO {
    @Query("select * from EDGE_TB")
    fun getAll(): List<RoomEdge>

    @Insert(onConflict = REPLACE)
    fun insert(edge: RoomEdge)

    @Delete
    fun delete(edge: RoomEdge)
}