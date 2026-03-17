package com.ledvance.energy.manager.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.text.intl.LocaleList
import androidx.core.os.LocaleListCompat
import com.ledvance.energy.manager.model.Language
import com.ledvance.utils.CoroutineScopeUtils
import com.ledvance.utils.Storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.Locale

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/13/25 15:07
 * Describe : LanguageUtils
 */
object LanguageUtils {
    private const val TAG = "LanguageUtils"
    private const val KEY_LANGUAGE = "Language"

    /**
     * @author : jason yin
     * Email : j.yin@ledvance.com
     * Created date 2023/10/10 16:27
     * Describe : LanguageKtx
     */
    private val appSupportLanguages = arrayListOf(
        Language.English,
        Language.French,
        Language.Portuguese,
        Language.Spanish,
    )
    private val currentAppLanguageTag = MutableStateFlow("")
    private val currentSystemLanguageTag = MutableStateFlow("")

    fun getSupportLanguages(): List<Language> {
        return appSupportLanguages
    }

    fun getCurAppLanguageTagFlow(): StateFlow<String> {
        return currentAppLanguageTag
    }

    fun getCurSystemLanguageTagFlow(): StateFlow<String> {
        return currentSystemLanguageTag
    }

    fun setAppLanguage(languageTag: String) {
        Timber.tag(TAG).i("setAppLanguage: languageTag=$languageTag")
        try {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
            // 部分机型切换语言比较慢，需要延迟一下去更新UI
            CoroutineScopeUtils.launch {
                delay(200)
                currentAppLanguageTag.update { languageTag }
            }
            Storage.setValue(KEY_LANGUAGE, languageTag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateLanguageResourceConfiguration(context: Context) {
        Timber.tag(TAG).i("updateLanguageResourceConfiguration: context=$context")
        context.resources.apply {
            configuration.setLocale(Locale(getCurrentAppNonRegionLanguageTag()))
            updateConfiguration(configuration, displayMetrics)
        }
    }

    private fun getCurrentAppNonRegionLanguageTag(): String =
        AppCompatDelegate.getApplicationLocales().let {
            val languageTag = getLanguageNonRegionTag(getAppLanguageTag())
            if (appSupportLanguages.any { language -> language.tag == languageTag }) {
                languageTag
            } else Language.English.tag
        }.also {
            Timber.tag(TAG).i("getCurrentAppNonRegionLanguageTag: $it")
        }

    private fun getAppLanguageTag(): String = AppCompatDelegate.getApplicationLocales().let {
        it.get(0)?.toLanguageTag()
            ?: Storage.getValue(KEY_LANGUAGE, "").takeIf { it.isNotEmpty() }
            ?: Locale.getDefault().toLanguageTag()
    }.also {
        Timber.tag(TAG).i("getAppLanguageTag: $it")
    }

    fun initLanguageTag() = LocaleList.current.localeList.let { list ->
        // 经过测试验证通过setApplicationLocales只设置语言不设置地区时region会为null，而系统的不会
        val systemLanguageTag =
            list.find { it.region.isNotEmpty() }?.toLanguageTag() ?: Locale.getDefault()
                .toLanguageTag()
        Timber.tag(TAG).i("initLanguageTag: systemLanguageTag=$systemLanguageTag")
        currentAppLanguageTag.update { systemLanguageTag }
        val appLanguageTag = getCurrentAppNonRegionLanguageTag()
        Timber.tag(TAG).i("initLanguageTag: currentAppLanguageTag=$appLanguageTag")
        currentAppLanguageTag.update { appLanguageTag }
    }

    fun updateSystemLanguageTag(languageTag: String) {
        Timber.tag(TAG).i("updateSystemLanguageTag: languageTag=$languageTag")
        if (getLanguageRegionTag(languageTag).isEmpty()) {
            return
        }
        Timber.tag(TAG).i("updateSystemLanguageTag: successfully")
        currentSystemLanguageTag.update { languageTag }
    }

    fun getLanguageNonRegionTag(languageTag: String): String = languageTag.let {
        // 语言如果存在地区会用-拼接
        if (it.contains("-")) it.split("-")[0] else it
    }.also {
        Timber.tag(TAG).i("getLanguageNonRegionTag: $it")
    }

    fun getLanguageRegionTag(languageTag: String): String = languageTag.let {
        if (it.contains("-")) it.split("-")[1] else ""
    }.also {
        Timber.tag(TAG).i("getLanguageRegionTag: $it")
    }
}