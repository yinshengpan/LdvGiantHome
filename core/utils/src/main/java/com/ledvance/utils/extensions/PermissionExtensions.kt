package com.ledvance.utils.extensions

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ledvance.utils.AppContext

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/4/10 09:15
 * Describe : PermissionKtx
 */
private const val TAG = "PermissionKtx"

val BLUETOOTH_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
    )
} else {
    listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH
    )
}

fun checkBluetoothConnectPermissionsGranted(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return true
    }
    return checkPermissionGranted(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN
    )
}

fun checkPermissionGranted(vararg permissions: String): Boolean {
    permissions.forEach {
        val checkSelfPermission = ActivityCompat.checkSelfPermission(AppContext.get(), it)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

fun Context.checkIfPermissionsGrantedOnly(permissionList: List<String>): Boolean {
    val isBlocked = permissionList.any {
        ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }
    return !isBlocked
}

private val Context.locationManager get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager

fun Context.isLocationEnable(): Boolean {
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Context.checkLocationEnable(): Boolean {
    if (isLocationEnable() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        return true
    }
    tryCatch {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }
    return false
}

fun Context.isBluetoothEnable(): Boolean =
    (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).let {
        it.adapter != null && it.adapter.isEnabled
    }


fun Context.checkBluetoothEnable(): Boolean {
    if (isBluetoothEnable()) {
        return true
    }
    tryCatch {
        startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }
    return false
}