/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ledvance.energy.manager.model

sealed class DarkThemeMode(val mode: Int) {
    data object FollowSystem : DarkThemeMode(1)
    data object Light : DarkThemeMode(2)
    data object Dark : DarkThemeMode(3)

    companion object {
        fun valueOf(mode: Int?): DarkThemeMode {
            return when (mode) {
                FollowSystem.mode -> FollowSystem
                Light.mode -> Light
                Dark.mode -> Dark
                else -> Light
            }
        }
    }
}
