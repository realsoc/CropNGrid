package com.realsoc.cropngrid.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

typealias ExitTransitionLambda = (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)
typealias EnterTransitionLambda = (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)

data class Transitions(
    val enterTransitionChain: EnterTransitionLambda,
    val exitTransitionChain: ExitTransitionLambda,
    val popEnterTransitionChain: EnterTransitionLambda = enterTransitionChain,
    val popExitTransitionChain: ExitTransitionLambda = exitTransitionChain
) {

    fun addTransition(
        fromRoute: String,
        toRoute: String,
        transitions: Transitions
    ): Transitions {
        return addTransition(
            fromRoute,
            toRoute,
            transitions.enterTransitionChain,
            transitions.exitTransitionChain,
            transitions.popEnterTransitionChain,
            transitions.popExitTransitionChain
        )
    }
    private fun addTransition(
        fromRoute: String,
        toRoute: String,
        enterTransition: EnterTransitionLambda,
        exitTransition: ExitTransitionLambda,
        popEnterTransition: EnterTransitionLambda = enterTransition,
        popExitTransition: ExitTransitionLambda = exitTransition
    ): Transitions {
        return Transitions(
            mergeTransition(fromRoute, toRoute, enterTransition, enterTransitionChain),
            mergeTransition(fromRoute, toRoute, exitTransition, exitTransitionChain),
            mergeTransition(fromRoute, toRoute, popEnterTransition, popEnterTransitionChain),
            mergeTransition(fromRoute, toRoute, popExitTransition, popExitTransitionChain)
        )
    }

    @JvmName("mergeExitTransition")
    private fun mergeTransition(
        fromRoute: String,
        toRoute: String,
        transitionToApply: ExitTransitionLambda,
        transitionChain: ExitTransitionLambda
        ): ExitTransitionLambda {
        return {
            if (initialState.destination.route == fromRoute && targetState.destination.route == toRoute) {
                transitionToApply(this).plus(transitionChain(this))
            } else {
                transitionChain(this)
            }
        }
    }

    @JvmName("mergeEnterTransition")
    private fun mergeTransition(
        fromRoute: String,
        toRoute: String,
        transitionToApply: EnterTransitionLambda,
        transitionChain: EnterTransitionLambda
    ): EnterTransitionLambda {
        return {
            if (initialState.destination.route == fromRoute && targetState.destination.route == toRoute) {
                transitionToApply(this).plus(transitionChain(this))
            } else {
                transitionChain(this)
            }
        }
    }
    companion object {
        private val NONE_ENTER: EnterTransitionLambda = { EnterTransition.None }
        private val NONE_EXIT: ExitTransitionLambda = { ExitTransition.None }
        val BASE = Transitions(NONE_ENTER, NONE_EXIT, NONE_ENTER, NONE_EXIT)
    }
}

fun leftToRightTransition(easing: Easing, duration: Int): Transitions = Transitions(
    slideFromLeft(easing, duration),
    slideToRight(easing, duration)
)

fun rightToLeftTransition(easing: Easing, duration: Int): Transitions = Transitions(
    slideFromRight(easing, duration),
    slideToLeft(easing, duration)
)
fun slideFromLeft(
    easing: Easing,
    duration: Int
): EnterTransitionLambda {
    return {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.End,
            tween(duration, easing = easing),
            initialOffset = { offset -> offset * 3 / 2 }
        )
    }
}

fun slideFromRight(
    easing: Easing,
    duration: Int
): EnterTransitionLambda {
    return {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Start,
            tween(duration, easing = easing)
        )
    }
}

fun slideToLeft(
    easing: Easing,
    duration: Int
): ExitTransitionLambda {
    return {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Start,
            tween(duration, easing = easing)
        )
    }
}

fun slideToRight(
    easing: Easing,
    duration: Int
): ExitTransitionLambda {
    return {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.End,
            tween(duration, easing = easing),
            targetOffset = { offset -> offset * 3 / 2 }
        )
    }
}