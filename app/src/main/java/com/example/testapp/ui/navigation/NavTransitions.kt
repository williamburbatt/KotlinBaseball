package com.example.testapp.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

/**
 * Standard navigation transitions for the app.
 * Moving these here makes MainActivity cleaner and provides a single place
 * to tweak the "feel" of every screen transition.
 */
object NavTransitions {
    private const val Duration = 300

    val enter: () -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(Duration)
        ) + fadeIn(animationSpec = tween(Duration))
    }

    val exit: () -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(Duration)
        ) + fadeOut(animationSpec = tween(Duration))
    }

    val popEnter: () -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(Duration)
        ) + fadeIn(animationSpec = tween(Duration))
    }

    val popExit: () -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(Duration)
        ) + fadeOut(animationSpec = tween(Duration))
    }
}
