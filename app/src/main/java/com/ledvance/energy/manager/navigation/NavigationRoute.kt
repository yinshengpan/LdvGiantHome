package com.ledvance.energy.manager.navigation

import com.ledvance.energy.manager.model.License
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/19 13:36
 * Describe : NavigationRoute
 */
interface NavigationRoute

@Serializable
data object DeviceListRoute : NavigationRoute

@Serializable
data object LanguageRoute : NavigationRoute

@Serializable
data object OpenSourceLicensesRoute : NavigationRoute

@Serializable
data class LicenseContentRoute(val license: License) : NavigationRoute