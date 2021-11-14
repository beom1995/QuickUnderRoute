package kr.ac.myungji.quickunderroute.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["SRC", "DSTN"], tableName = "EDGE_TB")
class RoomEdge {
    @ColumnInfo(name = "SRC")
    val src: Int

    @ColumnInfo(name = "DSTN")
    val dstn: Int

    @ColumnInfo(name = "TIME_SEC")
    var timeSec: Int = 0

    @ColumnInfo(name = "DISTANCE_M")
    var distanceM: Int = 0

    @ColumnInfo(name = "FARE_WON")
    var fareWon: Int = 0

    constructor(src: Int, dstn: Int, timeSec: Int, distanceM: Int, fareWon: Int) {
        this.src = src
        this.dstn = dstn
        this.timeSec = timeSec
        this.distanceM = distanceM
        this.fareWon = fareWon
    }
}