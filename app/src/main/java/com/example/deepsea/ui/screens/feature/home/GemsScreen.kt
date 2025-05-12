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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GemsScreen(navController: NavController) {
    // State for gem count, dialogs, and animations
    var gemCount by remember { mutableStateOf(505) }
    var triggerParticle by remember { mutableStateOf(false) }
    var showPurchaseDialog by remember { mutableStateOf<String?>(null) }
    var dailyBonusClaimed by remember { mutableStateOf(false) }
    var showStreakDialog by remember { mutableStateOf(false) }
    var showPackageDialog by remember { mutableStateOf(false) }

    // Countdown timer for limited-time offer (24 hours)
    val offerEndTime by remember { mutableLongStateOf(System.currentTimeMillis() + 24 * 60 * 60 * 1000) }
    var timeRemaining by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            val remaining = offerEndTime - System.currentTimeMillis()
            if (remaining > 0) {
                val hours = (remaining / (1000 * 60 * 60)) % 24
                val minutes = (remaining / (1000 * 60)) % 60
                val seconds = (remaining / 1000) % 60
                timeRemaining = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                timeRemaining = "Offer Expired"
            }
            delay(1000)
        }
    }

    // Scroll state
    val scrollState = rememberScrollState()

    // Animation for gem icon scaling
    val gemScale = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        gemScale.animateTo(
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Banner animation
    val bannerScale = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        bannerScale.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Sparkle animation
    var showSparkle by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            showSparkle = true
            delay(500)
            showSparkle = false
        }
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
    val particleGems = remember { mutableListOf<ParticleGems>() }
    LaunchedEffect(triggerParticle) {
        if (triggerParticle) {
            repeat(20) {
                particleGems.add(
                    ParticleGems(
                        position = Offset(
                            Random.nextFloat() * 200 - 100,
                            Random.nextFloat() * 200 - 100
                        ),
                        velocity = Offset(
                            Random.nextFloat() * 8 - 4,
                            Random.nextFloat() * -6 - 2
                        ),
                        alpha = 1f,
                        life = 1f,
                        size = Random.nextFloat() * 8 + 2
                    )
                )
            }
            triggerParticle = false
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            particleGems.forEach { particle ->
                particle.update()
            }
            particleGems.removeAll { it.life <= 0f }
            delay(16) // 60fps
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gems & Rewards",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF58CC02)
                ),
                actions = {
                    IconButton(onClick = { /* Settings action */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Premium Offer Banner with Countdown
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(100.dp)
                    .scale(bannerScale.value)
                    .clickable {
                        showPackageDialog = true
                        trackAnalyticsEvent("premium_banner_clicked")
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFFFA500)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Premium",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "PREMIUM OFFER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "50% More Gems!",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Ends in:",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Text(
                                text = timeRemaining,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Gems Status Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Particles
                        Canvas(
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.Center)
                        ) {
                            particleGems.forEach { particle ->
                                drawCircle(
                                    color = Color(0xFFFFD700).copy(alpha = particle.alpha),
                                    radius = particle.size,
                                    center = particle.position + center
                                )
                            }
                        }
                        // Gem glow effect
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.Center)
                                .blur(if (showSparkle) 16.dp else 8.dp)
                                .background(
                                    Color(0xFFFFD700).copy(alpha = 0.5f),
                                    CircleShape
                                )
                        )
                        // Gem icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .scale(gemScale.value)
                                .align(Alignment.Center)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val path = Path()
                                val centerX = size.width / 2
                                val centerY = size.height / 2
                                val radius = size.width / 2
                                path.moveTo(centerX, centerY - radius)
                                path.lineTo(centerX + radius, centerY)
                                path.lineTo(centerX, centerY + radius)
                                path.lineTo(centerX - radius, centerY)
                                path.close()
                                drawPath(
                                    path = path,
                                    color = Color(0xFFFFD700),
                                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                                )
                                drawPath(
                                    path = path,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFFD700),
                                            Color(0xFFFFA500)
                                        )
                                    )
                                )

                                if (showSparkle) {
                                    val shineLength = radius * 0.7f
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.8f),
                                        start = Offset(centerX - shineLength / 2, centerY - shineLength / 2),
                                        end = Offset(centerX + shineLength / 2, centerY + shineLength / 2),
                                        strokeWidth = 4f
                                    )
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.8f),
                                        start = Offset(centerX + shineLength / 2, centerY - shineLength / 2),
                                        end = Offset(centerX - shineLength / 2, centerY + shineLength / 2),
                                        strokeWidth = 4f
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = "Your Gems",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = "$gemCount",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700),
                        letterSpacing = 0.5.sp
                    )
                    // Streak info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF0F0F0))
                            .padding(12.dp)
                            .clickable { showStreakDialog = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = "Streak",
                            tint = Color(0xFFFF4500),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Current Streak: 7 Days",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                            Text(
                                text = "Keep learning to earn more gems!",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                        }
                        IconButton(onClick = { showStreakDialog = true }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowForward,
                                contentDescription = "View Streak",
                                tint = Color(0xFF666666)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Daily Bonus Button
            ElevatedButton(
                onClick = {
                    if (!dailyBonusClaimed) {
                        gemCount += 10
                        triggerParticle = true
                        dailyBonusClaimed = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        vibrator?.vibrate(50)
                        trackAnalyticsEvent("daily_bonus_claimed")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = if (dailyBonusClaimed) Color(0xFFBBBBBB) else Color(0xFF58CC02),
                    contentColor = Color.White
                ),
                enabled = !dailyBonusClaimed,
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Daily Bonus",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (dailyBonusClaimed) "Daily Bonus Claimed!" else "Claim Daily Bonus +10 Gems",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Referral Program Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                        trackAnalyticsEvent("referral_card_clicked")
                        // TODO: Navigate to referral screen
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Refer Friends",
                        tint = Color(0xFF58CC02),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Invite Friends, Earn Gems!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = "Get 50 gems for each friend who joins using your link",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = "Go to Referral",
                        tint = Color(0xFF666666)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section header
            Text(
                text = "GET MORE GEMS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gem Purchase Options
            GemPurchaseOption(
                icon = Icons.Filled.Diamond,
                title = "Small Pack",
                subtitle = "Best for beginners",
                amount = "50",
                price = "$0.99",
                onClick = {
                    showPurchaseDialog = "50 Gems for $0.99"
                    trackAnalyticsEvent("purchase_initiated_50_gems")
                }
            )
            GemPurchaseOption(
                icon = Icons.Filled.Diamond,
                title = "Medium Pack",
                subtitle = "Most popular",
                amount = "100",
                price = "$1.99",
                isBestValue = false,
                onClick = {
                    showPurchaseDialog = "100 Gems for $1.99"
                    trackAnalyticsEvent("purchase_initiated_100_gems")
                }
            )
            GemPurchaseOption(
                icon = Icons.Filled.Diamond,
                title = "Large Pack",
                subtitle = "Best value",
                amount = "500",
                price = "$4.99",
                isBestValue = true,
                onClick = {
                    showPurchaseDialog = "500 Gems for $4.99"
                    trackAnalyticsEvent("purchase_initiated_500_gems")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Gem Store Button
            ElevatedButton(
                onClick = {
                    trackAnalyticsEvent("gem_store_clicked")
                    // TODO: Navigate to gem store screen
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color(0xFF1CB0F6),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Gem Store",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Visit Gem Store",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Earn Gems Section
            Text(
                text = "EARN FREE GEMS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Free Gem Earning Options
            EarnGemsOption(
                icon = Icons.Filled.EmojiEvents,
                title = "Complete Challenges",
                subtitle = "Earn up to 20 gems per challenge",
                onClick = {
                    trackAnalyticsEvent("challenges_clicked")
                    // TODO: Navigate to challenges
                }
            )
            EarnGemsOption(
                icon = Icons.Filled.Favorite,
                title = "Invite Friends",
                subtitle = "Get 50 gems for each friend who joins",
                onClick = {
                    trackAnalyticsEvent("invite_friends_clicked")
                    // TODO: Navigate to invite friends
                }
            )
            EarnGemsOption(
                icon = Icons.Filled.History,
                title = "View Gem History",
                subtitle = "Check your gem earning & spending",
                onClick = {
                    trackAnalyticsEvent("gem_history_clicked")
                    // TODO: Navigate to gem history
                }
            )

            // Spacer at the bottom for better scrolling
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Purchase Confirmation Dialog
    showPurchaseDialog?.let { purchaseInfo ->
        AlertDialog(
            onDismissRequest = { showPurchaseDialog = null },
            title = {
                Text(
                    "Confirm Purchase",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Do you want to purchase $purchaseInfo?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Purchase",
                            tint = Color(0xFF58CC02)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Your gems will be added immediately",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (purchaseInfo) {
                            "50 Gems for $0.99" -> gemCount += 50
                            "100 Gems for $1.99" -> gemCount += 100
                            "500 Gems for $4.99" -> gemCount += 500
                        }
                        triggerParticle = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        vibrator?.vibrate(50)
                        showPurchaseDialog = null
                        trackAnalyticsEvent("purchase_completed_$purchaseInfo")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58CC02)
                    )
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showPurchaseDialog = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Streak Dialog
    if (showStreakDialog) {
        AlertDialog(
            onDismissRequest = { showStreakDialog = false },
            title = {
                Text(
                    "Learning Streak",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = "Streak",
                        tint = Color(0xFFFF4500),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "7 Day Streak!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Keep your streak going to earn more gems. You'll get +1 gem for each day, with bonuses at milestones!",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(7) { day ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            if (day < 7) Color(0xFF58CC02) else Color.LightGray,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${day + 1}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = when (day) {
                                        0 -> "M"
                                        1 -> "T"
                                        2 -> "W"
                                        3 -> "T"
                                        4 -> "F"
                                        5 -> "S"
                                        else -> "S"
                                    },
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStreakDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58CC02)
                    )
                ) {
                    Text("Keep Learning")
                }
            }
        )
    }

    // Premium Package Dialog
    if (showPackageDialog) {
        AlertDialog(
            onDismissRequest = { showPackageDialog = false },
            title = {
                Text(
                    "Premium Offer",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = "Premium",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unlock Premium Benefits!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PremiumBenefit(
                            text = "50% More Gems on All Purchases",
                            icon = Icons.Filled.Diamond
                        )
                        PremiumBenefit(
                            text = "Ad-Free Experience",
                            icon = Icons.Filled.Star
                        )
                        PremiumBenefit(
                            text = "Exclusive Lessons & Content",
                            icon = Icons.Filled.EmojiEvents
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Limited Time: $timeRemaining",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4500)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Only $9.99/month",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPackageDialog = false
                        trackAnalyticsEvent("premium_purchase_initiated")
                        // TODO: Initiate subscription purchase
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58CC02)
                    )
                ) {
                    Text("Get Premium")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showPackageDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PremiumBenefit(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF58CC02),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF333333)
        )
    }
}

@Composable
fun GemPurchaseOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    amount: String,
    price: String,
    isBestValue: Boolean = false,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    if (isBestValue) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Best Value",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0xFFFF4500), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$amount Gems",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = price,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
fun EarnGemsOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF58CC02),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = "Go to $title",
                tint = Color(0xFF666666)
            )
        }
    }
}

data class ParticleGems(
    var position: Offset,
    var velocity: Offset,
    var alpha: Float,
    var life: Float,
    var size: Float
) {
    fun update() {
        position += velocity
        alpha -= 0.05f
        life -= 0.05f
    }
}

fun trackAnalyticsEvent(event: String) {
    // TODO: Integrate with Firebase Analytics or similar
    // Example: Firebase.analytics.logEvent(event, null)
}