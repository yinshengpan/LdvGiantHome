package com.ledvance.ble.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 15:26
 * Describe : Handshake
 */
data class Handshake(
    val address: String,
    val name: String,
    val sn: String,
    val user: Int,
    val userId: Int,
    val scheduleStartTime: String,
    val scheduleChargeCurrent: Int,//A
    val minChargeCurrent: Int,//A
    val maxChargeCurrent: Int,//A
    val firmwareVersion: String,
    val bleVersion: String,
    val chargeCount: Int,
    val faultCount: Int,
    val plugAndChargeEnabled: Boolean,
    val networkMode: Int,
    val boxType: Int,
)