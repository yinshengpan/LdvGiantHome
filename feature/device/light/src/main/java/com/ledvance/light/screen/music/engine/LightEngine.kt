package com.ledvance.light.screen.music.engine

import com.ledvance.light.screen.music.fft.AudioFeatures

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:20
 * Describe : LightEngine
 */
class LightEngine {

    private var currentStrategy: LightStrategy = ClassicStrategy()
    private var currentMode: LightMode = LightMode.CLASSIC
    private val strategyMap by lazy {
        mutableMapOf<LightMode, LightStrategy>()
    }

    fun setMode(mode: LightMode) {
        if (mode == currentMode) return

        currentMode = mode
        currentStrategy = getOrCreateStrategy(mode)
    }

    private fun getOrCreateStrategy(mode: LightMode): LightStrategy {
        return strategyMap.getOrPut(mode) {
            when (mode) {
                LightMode.CLASSIC -> ClassicStrategy()
                LightMode.PARTY -> PartyStrategy()
                LightMode.RELAX -> RelaxStrategy()
                LightMode.BRAND -> BrandStrategy()
            }
        }
    }

    fun process(features: AudioFeatures): LightState {
        return currentStrategy.map(features)
    }
}