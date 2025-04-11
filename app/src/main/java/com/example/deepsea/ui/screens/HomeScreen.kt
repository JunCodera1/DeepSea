package com.example.deepsea.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder

fun NavGraphBuilder.composableWithCompositionLocal(
  route: String,
  arguments: List<NamedNavArgument> = emptyList(),
  deepLinks: List<NavDeepLink> = emptyList(),
  enterTransition: (
    @JvmSuppressWildcards
  AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition
          )? ={
      fadeIn()
  }
){}