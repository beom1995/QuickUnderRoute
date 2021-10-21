package kr.ac.myungji.quickunderroute

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["src", "dstn"], tableName = "EDGE_TB")
class RoomEdge {
    @ColumnInfo
    var src: Int = 0

    @ColumnInfo
    var dstn: Int = 0

    @ColumnInfo
    var timeSec: Int = 0

    @ColumnInfo
    var distanceM: Int = 0

    @ColumnInfo
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