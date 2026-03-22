package com.ledvance.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.ledvance.log.LogManager
import com.ledvance.ui.R
import com.ledvance.ui.component.ItemView
import com.ledvance.ui.component.LedvancePrimaryScreen
import kotlinx.coroutines.launch

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : ProfileScreen
 */
@Composable
internal fun ProfileScreen(onNavigateToLicenses: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LedvancePrimaryScreen(
        title = "Personal Center",
    ) {

        ItemView(
            itemIconResId = R.drawable.ic_log,
            title = "Share Logs",
            showDivider = true,
            onContentClick = {
                scope.launch {
                    LogManager.shareAppLog(context)
                }
            }
        )
//        ItemView(
//            itemIconResId = R.drawable.ic_license,
//            title = "Open Source Licenses",
//            showDivider = true,
//            onContentClick = onNavigateToLicenses,
//        )
    }
}