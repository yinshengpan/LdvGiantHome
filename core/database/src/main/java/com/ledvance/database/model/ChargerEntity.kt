package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/18/25 11:44
 * Describe : Charger
 */
@Entity(
    tableName = "chargers",
    foreignKeys = [
        ForeignKey(
            entity = DeviceEntity::class,
            parentColumns = ["address"],
            childColumns = ["box_address"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("box_address")]
)
data class ChargerEntity(
    @PrimaryKey
    @ColumnInfo("charger_number")
    val chargerNumber: String,
    @ColumnInfo("box_address")
    val boxAddress: String,
    @ColumnInfo("box_sn")
    val boxSn: String,
    @ColumnInfo("charge_current_value", defaultValue = "0")
    val chargeCurrent: String,
    @ColumnInfo("is_paired")
    val isPaired: Boolean,
    @ColumnInfo("is_online",defaultValue = "0")
    val isOnline: Boolean
)