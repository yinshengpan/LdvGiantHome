package com.ledvance.music.effect

import com.ledvance.music.analyzer.AudioFeature

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:32
 * Describe : LightEffectProvider
 */
internal interface LightEffectProvider {
    fun calculateLightState(features: AudioFeature): LightEffectState
}