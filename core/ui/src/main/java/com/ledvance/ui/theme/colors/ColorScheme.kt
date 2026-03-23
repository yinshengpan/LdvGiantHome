package com.ledvance.ui.theme.colors

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/13 16:13
 * Describe : ThemeColors
 */
@Immutable
class ColorScheme(
    val primary: Color,
    val primaryContent: Color,
    val primaryBackground: Color,
    val title: Color,
    val secondaryTitle: Color,
    val body: Color,
    val divider: Color,
    val secondaryDivider: Color,
    val screenBackground: Color,

    val buttonBackground: Color,
    val buttonContent: Color,
    val buttonBorderBrush: Brush,

    val dialogTitle: Color,
    val dialogMessage: Color,
    val dialogNegative: Color,
    val dialogPositive: Color,
    val dialogBackground: Color,
    val dialogSecondaryBackground: Color,

    val popupBackground: Color,
    val popupSecondaryBackground: Color,

    val textFieldContent: Color,
    val textFieldHint: Color,
    val textFieldUnit: Color,
    val textFieldBorder: Color,
    val textFieldSecondaryBorder: Color,
    val textFieldBackground: Color,

    val screenSecondaryBackground: Color,
    val progress: Color,
    val progressBackground: Color,
)

fun lightColorScheme(
    primary: Color = Orange,
    primaryContent: Color = Color.White,
    primaryBackground: Color = Orange,
    title: Color = Color.Black,
    secondaryTitle: Color = Black333333,
    body: Color = Grey666666,
    divider: Color = Gray4A3C3C43,
    secondaryDivider: Color = GrayBEBEBE,
    screenBackground: Color = Color.White,
    screenSecondaryBackground: Color = GreyF3F3F3,
    buttonContent: Color = Color.White,
    buttonBackground: Color = Orange,
    buttonBorderBrush: Brush = Brush.horizontalGradient(
        listOf(Color.Transparent, Color.Transparent)
    ),
    dialogTitle: Color = Color.Black,
    dialogMessage: Color = Color.Black,
    dialogNegative: Color = Grey666666,
    dialogPositive: Color = Orange,
    dialogBackground: Color = Color.White,
    dialogSecondaryBackground: Color = Color.White,
    popupBackground: Color = Color.White,
    popupSecondaryBackground: Color = OrangeFFE0CB,
    textFieldContent: Color = Color.Black,
    textFieldHint: Color = Grey9C9D9F,
    textFieldUnit: Color = Color.Black,
    textFieldBorder: Color = WhiteDDDDDD,
    textFieldSecondaryBorder: Color = GreyC4C4C4,
    textFieldBackground: Color = GrayCCF2F2F2,
    progress: Color = Green03CC00,
    progressBackground: Color = GrayF6F6F6,
): ColorScheme {
    return ColorScheme(
        primary = primary,
        primaryContent = primaryContent,
        primaryBackground = primaryBackground,
        title = title,
        secondaryTitle = secondaryTitle,
        body = body,
        divider = divider,
        secondaryDivider = secondaryDivider,
        screenBackground = screenBackground,
        buttonBackground = buttonBackground,
        buttonBorderBrush = buttonBorderBrush,
        buttonContent = buttonContent,
        dialogTitle = dialogTitle,
        dialogMessage = dialogMessage,
        dialogNegative = dialogNegative,
        dialogPositive = dialogPositive,
        dialogBackground = dialogBackground,
        dialogSecondaryBackground = dialogSecondaryBackground,
        popupBackground = popupBackground,
        popupSecondaryBackground = popupSecondaryBackground,
        textFieldContent = textFieldContent,
        textFieldHint = textFieldHint,
        textFieldUnit = textFieldUnit,
        textFieldBorder = textFieldBorder,
        textFieldSecondaryBorder = textFieldSecondaryBorder,
        screenSecondaryBackground = screenSecondaryBackground,
        textFieldBackground = textFieldBackground,
        progress = progress,
        progressBackground = progressBackground,
    )
}

fun darkColorScheme(
    primary: Color = OrangeFF8857,
    primaryContent: Color = Color.White,
    primaryBackground: Color = Orange,
    title: Color = Color.White,
    secondaryTitle: Color = WhiteDDDDDD,
    body: Color = Grey666666,
    divider: Color = GrayBEBEBE,
    secondaryDivider: Color = Black262B36,
    screenBackground: Color = BLACK1E1F23,
    screenSecondaryBackground: Color = Black30343C,
    buttonBackground: Color = Black30343C,
    buttonContent: Color = OrangeFF8857,
    buttonBorderBrush: Brush = Brush.horizontalGradient(
        listOf(OrangeFF8857, Purple63335E)
    ),
    dialogTitle: Color = Color.Black,
    dialogMessage: Color = Color.Black,
    dialogNegative: Color = Color.Black,
    dialogPositive: Color = OrangeFF8857,
    dialogBackground: Color = GrayCCF2F2F2,
    dialogSecondaryBackground: Color = Color.White,
    popupBackground: Color = BLACK515157,
    popupSecondaryBackground: Color = OrangeFF8857,
    textFieldContent: Color = Color.White,
    textFieldHint: Color = Grey9C9D9F,
    textFieldUnit: Color = GreyCCCCCC,
    textFieldBorder: Color = GreyC4C4C4,
    textFieldSecondaryBorder: Color = Grey666666,
    textFieldBackground: Color = Black30343C,
    progress: Color = Green03CC00,
    progressBackground: Color = GrayF6F6F6,
): ColorScheme {
    return ColorScheme(
        primary = primary,
        primaryContent = primaryContent,
        primaryBackground = primaryBackground,
        title = title,
        secondaryTitle = secondaryTitle,
        body = body,
        divider = divider,
        secondaryDivider = secondaryDivider,
        screenBackground = screenBackground,
        buttonBackground = buttonBackground,
        buttonContent = buttonContent,
        buttonBorderBrush = buttonBorderBrush,
        dialogTitle = dialogTitle,
        dialogMessage = dialogMessage,
        dialogNegative = dialogNegative,
        dialogPositive = dialogPositive,
        dialogBackground = dialogBackground,
        dialogSecondaryBackground = dialogSecondaryBackground,
        popupBackground = popupBackground,
        popupSecondaryBackground = popupSecondaryBackground,
        textFieldContent = textFieldContent,
        textFieldHint = textFieldHint,
        textFieldUnit = textFieldUnit,
        textFieldBorder = textFieldBorder,
        textFieldSecondaryBorder = textFieldSecondaryBorder,
        screenSecondaryBackground = screenSecondaryBackground,
        textFieldBackground = textFieldBackground,
        progress = progress,
        progressBackground = progressBackground,
    )
}