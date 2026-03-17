package com.ledvance.ui.component

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/12 09:11
 * Describe : Lottie
 */
@Composable
fun LottieAsset(
    assetName: String,
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset(assetName))
    LottieComponent(
        composition = composition,
        modifier = modifier,
        speed = speed,
        iterations = iterations
    )
}

@Composable
fun LottieRaw(
    @RawRes rawResId: Int,
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(rawResId))
    LottieComponent(
        composition = composition,
        modifier = modifier,
        speed = speed,
        iterations = iterations
    )
}

@Composable
fun LottieFile(
    fileName: String,
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.File(fileName))
    LottieComponent(
        composition = composition,
        modifier = modifier,
        speed = speed,
        iterations = iterations
    )
}

@Composable
fun LottieUrl(
    url: String,
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.Url(url))
    LottieComponent(
        composition = composition,
        modifier = modifier,
        speed = speed,
        iterations = iterations
    )
}

@Composable
private fun LottieComponent(
    composition: LottieComposition?,
    iterations: Int,
    modifier: Modifier = Modifier,
    speed: Float = 1f,
) {
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        speed = speed
    )
    LottieAnimation(composition = composition, progress = { progress }, modifier = modifier)
}