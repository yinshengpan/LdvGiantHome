package com.ledvance.light.screen.music.effect

import com.ledvance.light.screen.music.analyzer.AudioFeature

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:32
 * Describe : LightEffectProvider
 */
interface LightEffectProvider {
    fun calculateLightState(features: AudioFeature): LightEffectState
}