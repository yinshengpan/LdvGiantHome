package com.ledvance.usecase.repo

import android.content.Context
import com.ledvance.domain.bean.License
import com.ledvance.utils.extensions.tryCatchReturn
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/20/25 09:24
 * Describe : LicensesRepo
 */
@Singleton
class LicensesRepo @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val licensesFlow = MutableStateFlow<List<License>>(listOf())

    fun getLicensesFlow(): StateFlow<List<License>> = licensesFlow

    suspend fun syncLicenses() = withContext(Dispatchers.IO) {
        val libs = tryCatchReturn { Libs.Builder().withContext(context).build() }
        val licenses = libs?.libraries?.distinctBy { it.name }?.filter {
            !it.name.contains(".")
        }?.mapNotNull {
            val license = it.licenses.firstOrNull()?.takeIf {
                !it.url.isNullOrEmpty() || !it.licenseContent.isNullOrEmpty()
            } ?: return@mapNotNull null
            License(
                name = license.name,
                url = license.url ?: "",
                content = license.licenseContent ?: "",
                libName = it.name,
                libDescription = it.description ?: "",
                libVersion = it.artifactVersion ?: "",
                libWebsite = it.website ?: "",
                libUniqueId = it.uniqueId
            )
        } ?: listOf()
        licensesFlow.update { licenses }
    }
}
