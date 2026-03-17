package com.ledvance.energy.manager.state

import androidx.compose.runtime.State

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/11 14:31
 * Describe : EnableState
 */
interface EnableState : State<EnableState> {
    fun hasEnable(autoShowRequestDialog: Boolean = true): Boolean
}