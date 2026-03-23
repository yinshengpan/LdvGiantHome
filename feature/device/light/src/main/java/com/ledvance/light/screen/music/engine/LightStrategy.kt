package com.ledvance.light.screen.music.engine

import com.ledvance.light.screen.music.fft.AudioFeatures

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:32
 * Describe : LightStrategy
 */
interface LightStrategy {
    fun map(features: AudioFeatures): LightState
}