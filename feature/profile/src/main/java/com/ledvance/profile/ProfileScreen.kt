package com.ledvance.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ledvance.log.LogManager
import com.ledvance.ui.CardView
import com.ledvance.ui.R
import com.ledvance.ui.component.ItemView
import com.ledvance.ui.component.LedvancePrimaryScreen
import com.ledvance.utils.extensions.getVersionCode
import com.ledvance.utils.extensions.getVersionName
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
    var loading by remember { mutableStateOf(false) }
    val currentVersion = remember {
        "${context.getVersionName()}(${context.getVersionCode()})"
    }
    LedvancePrimaryScreen(
        title = "Personal Center",
        isLoading = loading,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CardView(paddingValues = PaddingValues(20.dp)) {
                ItemView(
                    itemIconResId = R.drawable.ic_log,
                    title = "Share Logs",
                    showDivider = true,
                    onContentClick = {
                        scope.launch {
                            loading = true
                            LogManager.shareAppLog(context)
                            loading = false
                        }
                    }
                )

                ItemView(
                    itemIconResId = R.drawable.ic_license,
                    title = "Open Source Licenses",
                    showDivider = true,
                    onContentClick = onNavigateToLicenses,
                )

                ItemView(
                    itemIconResId = R.drawable.ic_cur_version,
                    title = "Current Version",
                    content = currentVersion
                )
            }
        }
    }
}