package com.ledvance.energy.manager.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.ble.DLBBleUseCase
import com.ledvance.ble.bean.ConnectStatus
import com.ledvance.ble.bean.DeviceConfiguration
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.ble.bean.WhiteListState
import com.ledvance.database.model.ChargerEntity
import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.model.SetTripCurrentType
import com.ledvance.database.repo.ChargerRepo
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.database.repo.SetTripCurrentHistoryRepo
import com.ledvance.energy.manager.utils.toDevice
import com.ledvance.utils.DeviceManager
import com.ledvance.utils.extensions.to1Decimal
import com.ledvance.utils.extensions.toIntValue
import com.ledvance.utils.extensions.tryCatch
import com.ledvance.utils.extensions.tryCatchReturn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BleViewModel @Inject constructor(
    private val bleUseCase: DLBBleUseCase,
    private val deviceRepo: DeviceRepo,
    private val chargerRepo: ChargerRepo,
    private val setTripCurrentHistoryRepo: SetTripCurrentHistoryRepo,
) : ViewModel() {
    private val TAG = "BleViewModel"
    private val _deviceOnline = MutableStateFlow<Boolean>(false)
    private val _scanResult = MutableStateFlow<List<ScannedDevice>>(emptyList())
    private val _addChargerRestarting = MutableStateFlow<String?>(null)
    val addChargerRestarting: StateFlow<String?> = _addChargerRestarting
    val scanDevices = _scanResult.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf()
    )
    private var scanJob: Job? = null
    private var connectingJob: Job? = null
    private var currentPolling: Job? = null
    private var currentAddCharger: Job? = null
    private val bleMutex = Mutex()

    @Volatile
    private var canPolling: Boolean = true

    fun setCanPolling(can: Boolean) {
        canPolling = can
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startBleScan() {
        stopBleScan()
        Timber.tag(TAG).i("startBleScan")
        viewModelScope.launch(Dispatchers.IO) {
            scanJob = bleUseCase.startScan().onEach { devices ->
                Timber.tag(TAG)
                    .d("startBleScan scanResult-> ${devices.joinToString(",") { it.name }}")
                _scanResult.update {
                    devices.filter { !it.hasOutOfRange(SystemClock.elapsedRealtime()) }
                        .map { it.copy(scanTime = 0) }
                }
            }.catch {
                Timber.tag(TAG).e(it, "startBleScan")
            }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
        }
    }

    fun stopBleScan() {
        scanJob?.also {
            Timber.tag(TAG).i("stopBleScan")
            scanJob?.cancel()
            scanJob = null
        }
    }

    fun getConnectStatusFlow(): StateFlow<ConnectStatus> {
        return bleUseCase.getConnectStatusFlow()
    }

    fun getDeviceOnlineFlow(): StateFlow<Boolean> {
        return _deviceOnline
    }

    fun cancelConnecting() {
        connectingJob?.also {
            Timber.tag(TAG).i("cancelConnecting")
            connectingJob?.cancel()
            connectingJob = null
            _deviceOnline.update { false }
        }
    }

    fun disconnect() {
        cancelConnecting()
        Timber.tag(TAG).i("disconnect")
        bleUseCase.disconnect()
        _deviceOnline.update { false }
    }

    @SuppressLint("MissingPermission")
    private suspend fun sendHandshake(after: suspend () -> Unit = {}) {
        Timber.tag(TAG).i("sendHandshake")
        val sendHandshake = bleUseCase.sendHandshake()
        bleUseCase.getDevice()?.also { device ->
            if (sendHandshake == null) {
                DeviceManager.removeSN(address = device.address)
            } else {
                DeviceManager.setSN(device.address, device.sn)
            }
        }
        sendHandshake?.toDevice()?.also { device ->
            val localDevice = deviceRepo.getDevice(device.address)
            val newDevice = localDevice?.let {
                device.copy(
                    l1 = it.l1,
                    l2 = it.l2,
                    chargeCurrent = it.chargeCurrent,
                    tripCurrent = it.tripCurrent
                )
            } ?: device
            deviceRepo.addDevice(newDevice)
            after.invoke()
            _deviceOnline.update { true }
        }
    }

    fun connectDevice(device: ScannedDevice) {
        connect(device) {
            if (it) {
                sendHandshake {
                    startPolling()
                }
            }
        }
    }

    fun connect(device: ScannedDevice, after: suspend (Boolean) -> Unit = {}) {
        stopBleScan()
        cancelConnecting()
        Timber.tag(TAG).i("connect ${device.name}(${device.address})")
        connectingJob = viewModelScope.launch {
            val isConnected = bleUseCase.connect(
                scannedDevice = device, viewModelScope = viewModelScope
            )
            Timber.tag(TAG).i("connect ${device.name}(${device.address}) isConnected:$isConnected")
            after.invoke(isConnected)
        }
    }

    suspend fun setTripCurrent(value: String): Boolean {
        Timber.tag(TAG).i("setTripCurrent $value")
        val device = bleUseCase.getDevice() ?: return let {
            Timber.tag(TAG).e("setTripCurrent device is null")
            false
        }
        val tripCurrent = value.toIntOrNull()
        if (tripCurrent == null) {
            Timber.tag(TAG).e("setTripCurrent value is not digit")
            return false
        }
        currentAddCharger?.cancel()
        setCanPolling(false)
        val isSuccessfully = try {
            bleMutex.withLock {
                val newValue = (tripCurrent * 100).toString()
                val success = bleUseCase.setConfiguration(DeviceConfiguration.TripCurrent, newValue)
                Timber.tag(TAG).i("setTripCurrent $value isSuccessfully:$success")
                if (success) {
                    val tripCurrentValue = value.toIntOrNull() ?: 0
                    deviceRepo.updateTripCurrent(device.address, tripCurrentValue)
                    setTripCurrentHistoryRepo.addHistory(
                        device.sn,
                        tripCurrentValue,
                        SetTripCurrentType.Ble
                    )
                }
                success
            }
        } finally {
            setCanPolling(true)
        }
        return isSuccessfully
    }

    suspend fun setWhiteListState(state: WhiteListState): Boolean {
        Timber.tag(TAG).i("setWhiteListState $state")
        return bleMutex.withLock {
            val isSuccessfully =
                bleUseCase.setConfiguration(DeviceConfiguration.WhiteList, state.value.toString())
            Timber.tag(TAG).i("setWhiteListState $state isSuccessfully:$isSuccessfully")
            isSuccessfully
        }
    }

    suspend fun operationCharger(charger: ChargerEntity): Boolean {
        currentAddCharger?.cancel()
        setCanPolling(false)
        val chargerNumber = charger.chargerNumber
        val isDelete = charger.isPaired
        Timber.tag(TAG).i("operationCharger($chargerNumber) isDelete:$isDelete")
        val device = bleUseCase.getDevice() ?: return let {
            Timber.tag(TAG).e("operationCharger($chargerNumber) device is null")
            setCanPolling(true) // Ensure polling is reset if we return early
            false
        }
        val isSuccessfully = tryCatchReturn {
            bleMutex.withLock {
                val success = bleUseCase.editChargingPile(chargerNumber, isDelete)
                Timber.tag(TAG).i("operationCharger($chargerNumber) isSuccessfully:$success")
                if (success) {
                    val address = device.address
                    val pairedChargers =
                        chargerRepo.getChargerList(address, isPaired = true).toMutableList()
                    val availableChargers =
                        chargerRepo.getChargerList(address, isPaired = false).toMutableList()
                    if (isDelete) {
                        pairedChargers.removeIf { it.chargerNumber == chargerNumber }
                        availableChargers.add(charger.copy(isPaired = false, isOnline = false))
                    } else {
                        _addChargerRestarting.update { chargerNumber }
                        pairedChargers.add(charger.copy(isPaired = true, isOnline = false))
                        availableChargers.removeIf { it.chargerNumber == chargerNumber }
                    }
                    chargerRepo.update(pairedChargers + availableChargers)
                }
                success
            }
        } ?: false
        currentAddCharger = viewModelScope.launch {
            // 因为通士达充电桩添加后设备会重启，所以需要优先遍历请求充电桩的状态
            if (!isDelete && isSuccessfully) {
                tryCatch {
                    repeat(20) {
                        if (!isActive) {
                            Timber.tag(TAG).i("operationCharger querying charger state repeat end")
                            return@repeat
                        }
                        Timber.tag(TAG).i("operationCharger querying charger state $it")
                        val localDevice = deviceRepo.getDevice(device.address) ?: return@repeat
                        val list = localDevice.queryingChargerList(isPaired = true)
                        Timber.tag(TAG).i("operationCharger querying charger state list $list")
                        val contains = list.contains(chargerNumber)
                        Timber.tag(TAG)
                            .i("operationCharger querying charger state contains $contains chargerNumber:$chargerNumber")
                        if (contains) {
                            Timber.tag(TAG).i("operationCharger querying charger state repeat end")
                            return@repeat
                        }
                        delay(500)
                    }
                }
            }
            setCanPolling(true)
        }
        return isSuccessfully
    }

    fun startPolling() {
        Timber.tag(TAG).i("startPolling")
        currentPolling = startCurrentPolling()
    }

    fun stopPolling() {
        currentPolling?.also {
            Timber.tag(TAG).i("stopPolling")
            currentPolling?.cancel()
            currentPolling = null
        }
    }

    private fun startCurrentPolling(): Job = viewModelScope.launch {
        // 默认开启白名单
//        setWhiteListState(WhiteListState.Enabled)
        // 查询充电桩列表/查询实时电流信息5s一次
        val pollingTime = 5000L
        while (isActive) {
            Timber.tag(TAG).i("startCurrentPolling begin")
            val device = bleUseCase.getDevice()
            var localDevice = deviceRepo.getDevice(device?.address ?: "")
            if (device == null || localDevice == null) {
                Timber.tag(TAG).e("startCurrentPolling device is null localDevice:$localDevice")
                delay(pollingTime)
                continue
            }
            Timber.tag(TAG).i("startCurrentPolling querying values...")
            if (!checkCanPolling(pollingTime)) continue
            localDevice = localDevice.queryingTripCurrent()
            if (!checkCanPolling(pollingTime)) continue
            localDevice = localDevice.queryingL1()
            if (!checkCanPolling(pollingTime)) continue
            localDevice = localDevice.queryingL2()
            deviceRepo.updateDevice(localDevice)
            if (!checkCanPolling(pollingTime)) continue
            localDevice.queryingChargeCurrent()
            if (!checkCanPolling(pollingTime)) continue
            localDevice.queryingChargerList(isPaired = false)
            if (!checkCanPolling(pollingTime)) continue
            localDevice.queryingChargerList(isPaired = true)
            Timber.tag(TAG).i("startCurrentPolling querying end")
            delay(pollingTime)
            Timber.tag(TAG).i("startCurrentPolling end")
        }
    }

    private suspend fun checkCanPolling(timeMillis: Long = 5000): Boolean {
        val connected = bleUseCase.isConnected()
        _deviceOnline.update { connected }
        if (!canPolling || !connected) {
            Timber.tag(TAG).i("startCurrentPolling canPolling:$canPolling connected:$connected end")
            delay(timeMillis)
        }
        return canPolling
    }

    private suspend fun DeviceEntity.queryingL1(): DeviceEntity {
        Timber.tag(TAG).i("startCurrentPolling queryingL1 begin")
        val configuration =
            bleMutex.withLock { bleUseCase.getConfiguration(DeviceConfiguration.L1) }
        Timber.tag(TAG).i("startCurrentPolling queryingL1 end ${configuration?.getValue()}")
        return this.copy(l1 = configuration?.getValue()?.to1Decimal() ?: this.l1)
    }

    private suspend fun DeviceEntity.queryingL2(): DeviceEntity {
        Timber.tag(TAG).i("startCurrentPolling queryingL2 begin")
        val configuration =
            bleMutex.withLock { bleUseCase.getConfiguration(DeviceConfiguration.L2) }
        Timber.tag(TAG).i("startCurrentPolling queryingL2 end ${configuration?.getValue()}")
        return this.copy(l2 = configuration?.getValue()?.to1Decimal() ?: this.l2)
    }

    private suspend fun DeviceEntity.queryingTripCurrent(): DeviceEntity {
        Timber.tag(TAG).i("startCurrentPolling queryingTripCurrent begin")
        val configuration =
            bleMutex.withLock { bleUseCase.getConfiguration(DeviceConfiguration.TripCurrent) }
        val device = this.copy(
            tripCurrent = configuration?.getValue()?.toIntValue()?.toString() ?: this.tripCurrent
        )
        Timber.tag(TAG)
            .i("startCurrentPolling queryingTripCurrent end ${configuration?.getValue()}")
        return device
    }

    private suspend fun DeviceEntity.queryingChargeCurrent() {
        Timber.tag(TAG).i("startCurrentPolling queryingChargeCurrent begin")
        val pairedChargerList = chargerRepo.getChargerList(address, isPaired = true)
        val newPairedChargerList = pairedChargerList.map {
            if (!canPolling) {
                return@map it
            }
            val configuration = bleMutex.withLock {
                bleUseCase.getConfiguration(DeviceConfiguration.ChargeCurrent(it.chargerNumber))
            }
            val newChargeCurrent = configuration?.getValue()?.to1Decimal() ?: it.chargeCurrent
            Timber.tag(TAG)
                .i("queryingChargeCurrent charger ${it.chargerNumber} old:${it.chargeCurrent} new:${configuration?.getValue()}")
            it.copy(chargeCurrent = newChargeCurrent)
        }
        chargerRepo.update(newPairedChargerList)
        Timber.tag(TAG).i("startCurrentPolling queryingChargeCurrent end")
    }

    private suspend fun DeviceEntity.queryingChargerList(isPaired: Boolean): Set<String> {
        Timber.tag(TAG).i("startCurrentPolling queryingChargerList(isPaired:$isPaired) begin")
        val chargerList = bleMutex.withLock { bleUseCase.queryChargerPile(isPaired = isPaired) }
        val list = chargerList ?: if (isPaired) return setOf() else listOf()
        chargerRepo.updateChargerList(sn, address, isPaired, list)
        if (!isPaired) {
            chargerRepo.deleteNotIn(address, isPaired, list)
        } else {
            val value = _addChargerRestarting.value
            if (!value.isNullOrEmpty() && list.contains(value)) {
                Timber.tag(TAG)
                    .i("startCurrentPolling queryingChargerList(isPaired:$isPaired) add charger value:$value")
                _addChargerRestarting.update { null }
            }
        }
        Timber.tag(TAG).i("startCurrentPolling queryingChargerList(isPaired:$isPaired) end $list")
        return list.toSet()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.tag(TAG).i("viewModel onCleared")
        disconnect()
        stopPolling()
    }
}