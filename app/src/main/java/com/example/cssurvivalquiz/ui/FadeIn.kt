package com.example.cssurvivalquiz.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun FadeIn(
    modifier: Modifier = Modifier,
    durationMs: Int = 400,
    content: @Composable () -> Unit
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(durationMs))
    }

    androidx.compose.runtime.CompositionLocalProvider {
        androidx.compose.foundation.layout.Box(modifier.alpha(alpha.value)) {
            content()
        }
    }
}