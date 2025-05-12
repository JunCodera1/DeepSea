package com.example.deepsea.ui.screens.feature.home

import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import androidx.navigation.NavController
import kotlin.random.Random

@Composable
fun HeartsScreen(navController: NavController) {
    // State for heart count and particle effect
    var heartCount by remember { mutableStateOf(5) }
    var triggerParticle by remember { mutableStateOf(false) }

    // Animation for heart icon scaling
    val heartScale = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        heartScale.animateTo(
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Button press animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale = if (isPressed) 0.95f else 1f

    // Haptic feedback
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val vibrator = context.getSystemService<Vibrator>()

    // Particle effect state
    val particles = remember { mutableListOf<Particle>() }
    LaunchedEffect(triggerParticle) {
        if (triggerParticle) {
            repeat(10) {
                particles.add(
                    Particle(
                        position = Offset(0f, 0f),
                        velocity = Offset(
                            Random.nextFloat() * 4 - 2,
                            Random.nextFloat() * 4 - 2
                        ),
                        alpha = 1f,
                        life = 1f
                    )
                )
            }
            triggerParticle = false
        }
        particles.forEach { particle ->
            particle.update()
        }
        particles.removeAll { it.life <= 0f }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF4B4B), // Duolingo red for hearts
                        Color(0xFF1CB0F6) // Duolingo blue
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Heart icon with particle effect
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            particles.forEach { particle ->
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = particle.alpha),
                    radius = 4f,
                    center = particle.position + Offset(size.width / 2, size.height / 2)
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Heart Icon",
            tint = Color.White,
            modifier = Modifier
                .size(80.dp)
                .scale(heartScale.value)
                .clip(RoundedCornerShape(16.dp))
        )
        Text(
            text = "$heartCount Hearts",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable {
                    heartCount += 1
                    triggerParticle = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    vibrator?.vibrate(50)
                }
        )
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(top = 16.dp)
                .scale(buttonScale),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF58CC02), // Duolingo green
                contentColor = Color.White
            ),
            interactionSource = interactionSource
        ) {
            Text(
                text = "Back",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Particle class for sparkle effect
data class Particle(
    var position: Offset,
    var velocity: Offset,
    var alpha: Float,
    var life: Float
) {
    fun update() {
        position += velocity
        alpha -= 0.05f
        life -= 0.05f
    }
}