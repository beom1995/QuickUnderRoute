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