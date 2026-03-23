package com.ledvance.light.screen.music.fft

import com.ledvance.light.screen.music.engine.LightEngine
import com.ledvance.light.screen.music.engine.LightMode
import com.ledvance.light.screen.music.engine.LightState
import com.ledvance.utils.extensions.tryCatch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.channels.BufferOverflow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:21
 * Describe : AudioLightController
 */
object AudioLightController {

    private val _lightFlow = MutableSharedFlow<LightState>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val lightFlow = _lightFlow.asSharedFlow()

    private val engine = LightEngine()

    fun onAudio(fft: FloatArray, amplitude: Float, mode: LightMode = LightMode.CLASSIC) {
        tryCatch {
            engine.setMode(mode)
            val features = FeatureExtractor.from(fft, amplitude)
            val state = engine.process(features)
            _lightFlow.tryEmit(state)
        }
    }
}