package com.ledvance.connected.system.extensions

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import com.ledvance.utils.extensions.checkBluetoothConnectPermissionsGranted

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/30 14:15
 * Describe : SystemExtensions
 */
@RequiresPermission(value = Manifest.permission.BLUETOOTH_CONNECT, conditional = true)
fun Context.openBluetooth() {
    val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    intent.checkActivityContext(this)
    if (!checkBluetoothConnectPermissionsGranted()) {
        return
    }
    if (!intent.resolveActivity(this)) {
        return
    }
    startActivity(intent)
}

fun Context.openAppDetail() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", packageName, null)
    intent.checkActivityContext(this)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    }
    if (!intent.resolveActivity(this)) {
        return
    }
    startActivity(intent)
}

fun Context.openLocation() {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    intent.checkActivityContext(this)
    if (!intent.resolveActivity(this)) {
        return
    }
    startActivity(intent)
}
