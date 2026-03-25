package com.ledvance.light.screen.music.effect

import com.ledvance.light.screen.music.analyzer.AudioFeature

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:20
 * Describe : LightEffectEngine
 */
internal class LightEffectEngine {

    private var currentStrategy: LightEffectProvider = ClassicLightEffect()
    private var currentMode: LightEffectMode = LightEffectMode.CLASSIC
    private val strategyMap by lazy {
        mutableMapOf<LightEffectMode, LightEffectProvider>()
    }

    fun switchMode(mode: LightEffectMode) {
        if (mode == currentMode) return

        currentMode = mode
        timber.log.Timber.tag("LightEffectEngine").d("switchMode to: $mode")
        currentStrategy = getOrCreateStrategy(mode)
    }

    private fun getOrCreateStrategy(mode: LightEffectMode): LightEffectProvider {
        return strategyMap.getOrPut(mode) {
            when (mode) {
                LightEffectMode.CLASSIC -> ClassicLightEffect()
                LightEffectMode.PARTY -> PartyLightEffect()
                LightEffectMode.RELAX -> RelaxLightEffect()
                LightEffectMode.BRAND -> BrandLightEffect()
            }
        }
    }

    fun generateEffect(features: AudioFeature): LightEffectState {
        return currentStrategy.calculateLightState(features)
    }
}