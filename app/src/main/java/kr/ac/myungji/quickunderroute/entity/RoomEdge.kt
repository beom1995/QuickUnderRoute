package kr.ac.myungji.quickunderroute.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["SRC", "DSTN"], tableName = "EDGE_TB")
class RoomEdge {
    @ColumnInfo(name = "SRC")
    var src: Int = 0

    @ColumnInfo(name = "DSTN")
    var dstn: Int = 0

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

class MinEdgeByTime: Comparator<RoomEdge> {
    override fun compare(e1: RoomEdge, e2: RoomEdge): Int = e1.timeSec - e2.timeSec
}

class MinEdgeByDistance: Comparator<RoomEdge> {
    override fun compare(e1: RoomEdge, e2: RoomEdge): Int = e1.distanceM - e2.distanceM
}

class MinEdgeByFare: Comparator<RoomEdge> {
    override fun compare(e1: RoomEdge, e2: RoomEdge): Int = e1.fareWon - e2.fareWon
}