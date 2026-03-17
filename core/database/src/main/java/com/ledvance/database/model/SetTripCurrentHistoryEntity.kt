package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 17:21
 * Describe : SetTripCurrentHistory
 */
@Entity(tableName = "set_trip_current_history")
data class SetTripCurrentHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sn: String,
    @ColumnInfo(name = "trip_current")
    val tripCurrent: Int,
    @ColumnInfo(name = "create_time")
    val createTime: Long,
    val type: SetTripCurrentType,
) {
    @Ignore
    var createTimeStr: String = ""
}