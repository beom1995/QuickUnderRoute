package kr.ac.myungji.quickunderroute

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "STATION_TB")
class RoomStation {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo
    var no: Int = 0

    @ColumnInfo
    var lineNo: Int = 0

    @ColumnInfo
    var transferSt: Int = 0

    @ColumnInfo
    var timetable: String = ""

    constructor(no: Int, lineNo: Int, transferSt: Int, timetable: String) {
        this.no = no
        this.lineNo = lineNo
        this.transferSt = transferSt
        this.timetable = timetable
    }
}