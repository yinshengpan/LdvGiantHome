package com.ledvance.energy.manager.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.energy.manager.model.Language
import com.ledvance.energy.manager.utils.LanguageUtils
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/12/25 16:26
 * Describe : LanguageScreen
 */
@Composable
fun LanguageScreen(onBack: () -> Unit) {
    val appLanguageTag by LanguageUtils.getCurAppLanguageTagFlow()
        .collectAsStateWithLifecycle()
    val appSupportLanguages = remember { LanguageUtils.getSupportLanguages() }
    LedvanceScreen(title = stringResource(R.string.language), onBackPressed = onBack) {
        Card(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(7.dp),
        ) {
            LazyColumn {
                itemsIndexed(appSupportLanguages) { index, language ->
                    LanguageItem(language, appLanguageTag == language.tag) {
                        LanguageUtils.setAppLanguage(language.tag)
                    }
                    if (index != appSupportLanguages.lastIndex) {
                        HorizontalDivider(
                            color = AppTheme.colors.divider,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageItem(language: Language, isSelected: Boolean, onClick: (Language) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 15.dp)
            .clickable(onClick = { onClick.invoke(language) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = language.name,
            fontSize = 15.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Image(
                painter = painterResource(id = R.drawable.ic_language_selected),
                contentDescription = "selected",
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(20.dp)
            )
        }
    }
}