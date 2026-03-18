package com.ledvance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:51
 * Describe : PageLifecycleLogger
 */
private const val TAG = "PageLifecycle"

@Composable
fun PageLifecycleLogger(pageName: String) {
    DisposableEffect(pageName) {
        Timber.tag(TAG).d("Page shown: $pageName")
        onDispose {
            Timber.tag(TAG).d("Page destroyed: $pageName")
        }
    }
}