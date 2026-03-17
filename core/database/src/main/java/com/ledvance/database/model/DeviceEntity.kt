package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 17:57
 * Describe : DeviceEntity
 */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val address: String,
    val name: String,
    val sn: String,
    val user: Int,
    val userId: Int,
    @ColumnInfo(name = "l1_current_value", defaultValue = "0")
    val l1: String,
    @ColumnInfo(name = "l2_current_value", defaultValue = "0")
    val l2: String,
    @ColumnInfo(name = "trip_current_value", defaultValue = "0")
    val tripCurrent: String,
    @ColumnInfo(name = "charge_current_value", defaultValue = "0")
    val chargeCurrent: String,
    @ColumnInfo(name = "schedule_start_time")
    val scheduleStartTime: String,
    @ColumnInfo(name = "schedule_charge_current")
    val scheduleChargeCurrent: Int,//A
    @ColumnInfo(name = "min_charge_current")
    val minChargeCurrent: Int,//A
    @ColumnInfo(name = "max_charge_current")
    val maxChargeCurrent: Int,//A
    @ColumnInfo(name = "firmware_version")
    val firmwareVersion: String,
    @ColumnInfo(name = "ble_version")
    val bleVersion: String,
    @ColumnInfo(name = "charge_count")
    val chargeCount: Int,
    @ColumnInfo(name = "fault_count")
    val faultCount: Int,
    @ColumnInfo(name = "plug_and_charge_enabled")
    val plugAndChargeEnabled: Boolean,
    @ColumnInfo(name = "network_mode")
    val networkMode: NetworkMode,
    @ColumnInfo(name = "box_type")
    val boxType: BoxType,
    @ColumnInfo(defaultValue = "0")
    val index: Long = 0
)