package com.ledvance.music.analyzer

import com.ledvance.music.effect.LightEffectEngine
import com.ledvance.music.effect.LightEffectMode
import com.ledvance.music.effect.LightEffectState
import com.ledvance.utils.extensions.tryCatch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.channels.BufferOverflow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:21
 * Describe : AudioLightDispatcher
 */
internal object AudioLightDispatcher {

    private val _lightFlow = MutableSharedFlow<LightEffectState>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val lightFlow = _lightFlow.asSharedFlow()

    private val engine = LightEffectEngine()

    fun dispatchAudioData(fft: FloatArray, amplitude: Float, mode: LightEffectMode = LightEffectMode.CLASSIC) {
        tryCatch {
            engine.switchMode(mode)
            val features = AudioFeatureAnalyzer.extractFeatures(fft, amplitude)
            timber.log.Timber.tag("AudioLightDispatcher").d("dispatchAudioData: mode=$mode amplitude=$amplitude")
            val state = engine.generateEffect(features)
            _lightFlow.tryEmit(state)
        }
    }
}
