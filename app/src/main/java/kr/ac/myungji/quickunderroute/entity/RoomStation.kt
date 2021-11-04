package kr.ac.myungji.quickunderroute.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "STATION_TB")
class RoomStation {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "NO")
    var no: Int

    @ColumnInfo(name = "LINE_NO")
    var lineNo: Int?

    @ColumnInfo(name = "TRANSFER_ST", defaultValue = "0")
    var transferSt: Int

    @ColumnInfo(name = "TIMETABLE")
    var timetable: String?

    constructor(no: Int, lineNo: Int?, transferSt: Int, timetable: String?) {
        this.no = no
        this.lineNo = lineNo
        this.transferSt = transferSt
        this.timetable = timetable
    }
}