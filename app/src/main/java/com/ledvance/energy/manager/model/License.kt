package com.ledvance.energy.manager.model

import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/20/25 09:38
 * Describe : License
 */
@Serializable
data class License(
    val name: String,
    val url: String,
    val content: String,
    val libName: String,
    val libVersion: String,
    val libDescription: String,
    val libWebsite: String,
    val libUniqueId: String,
)