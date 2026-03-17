package com.ledvance.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import androidx.core.content.getSystemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/11 10:07
 * Describe : LocationManager
 */
object LocationManager {
    private const val TAG = "LocationManager"
    private val locationEnableFlow = MutableStateFlow(false)
    val locationEnable: StateFlow<Boolean> = locationEnableFlow

    fun initialize() {
        locationEnableFlow.tryEmit(hasLocationEnableForProvider())
        val filter = IntentFilter()
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        AppContext.get().registerReceiver(locationReceiver, filter)
    }

    fun hasLocationEnable(): Boolean = locationEnableFlow.value

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isLocationEnable = hasLocationEnableForProvider()
            if (isLocationEnable != locationEnableFlow.value) {
                Timber.tag(TAG).i("onReceive isLocationEnable=$isLocationEnable")
                locationEnableFlow.tryEmit(isLocationEnable)
            }
        }
    }

    private fun hasLocationEnableForProvider() = AppContext.get()
        .getSystemService<LocationManager>()?.let {
            it.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } ?: false
}