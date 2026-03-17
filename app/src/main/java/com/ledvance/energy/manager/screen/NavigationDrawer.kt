package com.ledvance.energy.manager.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.energy.manager.navigation.LanguageRoute
import com.ledvance.energy.manager.navigation.NavigationRoute
import com.ledvance.energy.manager.navigation.OpenSourceLicensesRoute
import com.ledvance.energy.manager.state.LedvanceAppState
import com.ledvance.giant.BuildConfig
import com.ledvance.log.LogManager
import com.ledvance.ui.R
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme
import kotlinx.coroutines.launch

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/12/25 13:08
 * Describe : DrawerScreen
 */
@Composable
fun NavigationDrawer(
    appState: LedvanceAppState,
    onGotoPage: (NavigationRoute) -> Unit,
    content: @Composable () -> Unit
) {
    val screenBackground = AppTheme.colors.screenBackground
    val chromeTabColor = AppTheme.colors.screenBackground
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = appState.drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = screenBackground,
                drawerContentColor = AppTheme.colors.title,
                modifier = Modifier.width(260.dp),
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                Spacer(modifier = Modifier.padding(top = 60.dp))
                NavigationDrawerItem(
                    title = stringResource(R.string.language),
                    icon = painterResource(R.mipmap.icon_language),
                    onClick = {
                        onGotoPage.invoke(LanguageRoute)
                        scope.launch { appState.drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    title = stringResource(R.string.privacy_policy),
                    icon = painterResource(R.mipmap.icon_policy),
                    onClick = {
//                        launchCustomChromeTab(
//                            context = context,
//                            uri = context.getString(com.ledvance.energy.manager.R.string.terms_of_use_url)
//                                .toUri(),
//                            toolbarColor = chromeTabColor.toArgb()
//                        )
                        scope.launch { appState.drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    title = stringResource(R.string.terms_of_use),
                    icon = painterResource(R.mipmap.icon_term),
                    onClick = {
//                        launchCustomChromeTab(
//                            context = context,
//                            uri = context.getString(com.ledvance.energy.manager.R.string.licenses_url)
//                                .toUri(),
//                            toolbarColor = chromeTabColor.toArgb()
//                        )
                        scope.launch { appState.drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    title = stringResource(R.string.open_source_licenses),
                    icon = painterResource(R.mipmap.icon_license),
                    onClick = {
                        onGotoPage.invoke(OpenSourceLicensesRoute)
                        scope.launch { appState.drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    title = "Set History",
                    icon = painterResource(R.mipmap.icon_set_history),
                    onClick = {
                        scope.launch { appState.drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    title = "Share Logs",
                    icon = painterResource(R.mipmap.icon_share_logs),
                    onClick = {
                        scope.launch { appState.drawerState.close() }
                        scope.launch { LogManager.shareAppLog(context) }
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${stringResource(R.string.version)} ${BuildConfig.VERSION_NAME}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    style = AppTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        content = content
    )
}

@Composable
private fun NavigationDrawerItem(title: String, icon: Painter, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .debouncedClickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = icon,
            contentDescription = title,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(18.dp),
            contentScale = ContentScale.FillWidth
        )
        Text(
            text = title,
            style = AppTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}