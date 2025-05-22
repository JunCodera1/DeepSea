package com.example.deepsea.ui.screens.feature.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deepsea.R
import com.example.deepsea.text.PrimaryText
import com.example.deepsea.text.TitleText
import com.example.deepsea.ui.components.*
import com.example.deepsea.ui.viewmodel.home.HomeViewModel
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UnitsListScreen(
    modifier: Modifier = Modifier,
    totalSectionCount: Int,
    section: SectionData,
    sections: List<SectionData>,
    state: LazyListState,
    sectionIndex: Int,
    units: List<UnitData>,
    starCountPerUnit: Int,
    completedStars: Map<Long, Set<Int>>,
    homeViewModel: HomeViewModel,
    navController: NavController,
    onJumpToSection: (sectionIndex: Int, unitIndex: Int) -> Unit,
    onGuideBookClicked: (unitId: Long) -> Unit,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean, unitId: Long, starIndex: Int) -> Unit,
    onStarComplete: (unitId: Long, starIndex: Int) -> Unit
) {
    Log.d("UnitsListScreen", "Completed stars: $completedStars")

    var isDialogShown by remember { mutableStateOf(false) }
    var selectedUnitId by remember { mutableLongStateOf(-1L) }
    var selectedStarIndex by remember { mutableIntStateOf(-1) }
    var dialogTransition by remember { mutableFloatStateOf(0f) }
    var isDialogInteractive by remember { mutableStateOf(false) }

    // Animated background
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "backgroundOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    ),
                    center = androidx.compose.ui.geometry.Offset.Infinite
                )
            )
    ) {
        // Floating particles background
        FloatingParticles()

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            state = state,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            items(units.size) { index ->
                val unit = units[index]
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(600, delayMillis = index * 100)
                    ) + fadeIn(animationSpec = tween(600, delayMillis = index * 100))
                ) {
                    Column {
                        EnhancedUnitHeader(
                            modifier = Modifier.fillMaxWidth(),
                            data = unit,
                            unitIndex = index,
                            onGuideBookClicked = { onGuideBookClicked(unit.id) }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        EnhancedUnitContent(
                            unitIndex = index,
                            starCount = starCountPerUnit,
                            unitImage = unit.image,
                            colorMain = unit.color,
                            colorDark = unit.darkerColor,
                            completedStars = completedStars[unit.id] ?: emptySet(),
                            unitId = unit.id,
                            onStarClicked = { coordinateInRoot, isInteractive, starIndex ->
                                selectedUnitId = unit.id
                                selectedStarIndex = starIndex
                                isDialogInteractive = isInteractive
                                dialogTransition = coordinateInRoot
                                isDialogShown = true
                                onStarClicked(coordinateInRoot, isInteractive, unit.id, starIndex)
                            }
                        )
                    }
                }
            }

            if (sectionIndex < totalSectionCount - 1) {
                item {
                    EnhancedSectionTransition(
                        nextSection = sections[sectionIndex + 1],
                        onJumpToSection = { onJumpToSection(sectionIndex + 1, 0) }
                    )
                }
            }
        }

        if (isDialogShown) {
            val xpAmount = (selectedStarIndex + 1) * 10
            StarDialog(
                isDialogShown = isDialogShown,
                isDialogInteractive = isDialogInteractive,
                dialogTransition = dialogTransition,
                navController = navController,
                xpAmount = xpAmount,
                unitId = selectedUnitId,
                starIndex = selectedStarIndex,
                onDismiss = { isDialogShown = false },
                onStarComplete = {
                    onStarComplete(selectedUnitId, selectedStarIndex)
                    isDialogShown = false
                }
            )
        }
    }
}

@Composable
fun FloatingParticles() {
    val particles = remember {
        (0..15).map {
            ParticleData(
                offsetX = (0..100).random().toFloat(),
                offsetY = (0..100).random().toFloat(),
                size = (2..8).random().dp,
                alpha = Random.nextFloat() * (0.4f - 0.1f) + 0.1f
            )
        }
    }

    particles.forEach { particle ->
        val infiniteTransition = rememberInfiniteTransition(label = "particle")
        val animatedY by infiniteTransition.animateFloat(
            initialValue = particle.offsetY,
            targetValue = particle.offsetY + 50f,
            animationSpec = infiniteRepeatable(
                animation = tween((3000..8000).random(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particleY"
        )

        Box(
            modifier = Modifier
                .offset(
                    x = particle.offsetX.dp,
                    y = animatedY.dp
                )
                .size(particle.size)
                .alpha(particle.alpha)
                .background(Color.White, CircleShape)
        )
    }
}

data class ParticleData(
    val offsetX: Float,
    val offsetY: Float,
    val size: androidx.compose.ui.unit.Dp,
    val alpha: Float
)

@Composable
fun EnhancedUnitContent(
    unitIndex: Int,
    starCount: Int,
    @DrawableRes unitImage: Int,
    colorMain: Color,
    colorDark: Color,
    completedStars: Set<Int>,
    unitId: Long,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean, starIndex: Int) -> Unit
) {
    var showLockedDialog by remember { mutableStateOf(false) }
    val pathProgress by remember { mutableStateOf(completedStars.size.toFloat() / starCount) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Constellation path background
            ConstellationPath(
                starCount = starCount,
                completedStars = completedStars,
                colorMain = colorMain,
                unitIndex = unitIndex
            )

            Column(verticalArrangement = Arrangement.spacedBy(40.dp)) {
                repeat(starCount) { starIndex ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val alignPercentage = orderToPercentage(starIndex, unitIndex % 2 == 0)
                        Spacer(modifier = Modifier.fillMaxWidth(alignPercentage))

                        val isCompleted = starIndex in completedStars
                        val isNextUnlocked = starIndex <= completedStars.size
                        val isInteractive = isCompleted || isNextUnlocked

                        Timber.tag("UnitContent")
                            .d("Unit $unitId, Star $starIndex: isCompleted=$isCompleted, isNextUnlocked=$isNextUnlocked, completedStars=$completedStars")

                        AnimatedVisibility(
                            visible = true,
                            enter = scaleIn(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = starIndex * 150
                                )
                            )
                        ) {
                            if (starIndex == 0 && unitIndex == 0 && completedStars.isEmpty()) {
                                EnhancedSelectableStarButton(
                                    isInitial = true,
                                    colorMain = colorMain,
                                    colorDark = colorDark,
                                    onStarClicked = { coordinateInRoot, _ ->
                                        onStarClicked(coordinateInRoot, true, starIndex)
                                    }
                                )
                            } else {
                                EnhancedStarButton(
                                    isCompleted = isCompleted,
                                    isUnlocked = isNextUnlocked,
                                    starIndex = starIndex,
                                    colorMain = colorMain,
                                    onStarClicked = { coordinateInRoot, _ ->
                                        onStarClicked(coordinateInRoot, isInteractive, starIndex)
                                    },
                                    onLockedStarClicked = { showLockedDialog = true }
                                )
                            }
                        }
                    }
                }
            }

            // Floating unit image with parallax effect
            val infiniteTransition = rememberInfiniteTransition(label = "unitImage")
            val floatOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "floatOffset"
            )

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(y = floatOffset.dp)
                    .align(
                        alignment = if (unitIndex % 2 == 0) Alignment.CenterEnd else Alignment.CenterStart
                    )
            ) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.Center)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    colorMain.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                        .blur(20.dp)
                )

                Image(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape),
                    painter = painterResource(id = unitImage),
                    contentDescription = "Unit image"
                )
            }

            LockedStarDialog(
                isVisible = showLockedDialog,
                onDismiss = { showLockedDialog = false }
            )
        }
    }
}

@Composable
fun ConstellationPath(
    starCount: Int,
    completedStars: Set<Int>,
    colorMain: Color,
    unitIndex: Int
) {
    // This would draw connecting lines between stars to create a constellation effect
    // Implementation would use Canvas to draw the connecting paths
}

@Composable
fun EnhancedStarButton(
    isCompleted: Boolean,
    isUnlocked: Boolean,
    starIndex: Int,
    colorMain: Color,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean) -> Unit,
    onLockedStarClicked: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "starScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isCompleted) 360f else 0f,
        animationSpec = tween(1000),
        label = "starRotation"
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .rotate(rotation)
    ) {
        if (isCompleted) {
            // Completed star with particle effects
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colorMain,
                                colorMain.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
                    .clickable { onStarClicked(0f, true) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_star_filled),
                    contentDescription = "Completed Star",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }
        } else if (isUnlocked) {
            // Unlocked star with pulsing animation
            val pulseScale by animateFloatAsState(
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colorMain.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
                    .border(2.dp, colorMain, CircleShape)
                    .clickable { onStarClicked(0f, true) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_star_outline),
                    contentDescription = "Available Star",
                    tint = colorMain,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.Center)
                )
            }
        } else {
            // Locked star
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
                    .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                    .clickable { onLockedStarClicked() }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_lock),
                    contentDescription = "Locked Star",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun EnhancedSelectableStarButton(
    isInitial: Boolean,
    colorMain: Color,
    colorDark: Color,
    onStarClicked: (coordinateInRoot: Float, isInteractive: Boolean) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "initialStar")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowIntensity"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        colorMain.copy(alpha = glowIntensity),
                        colorMain.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                CircleShape
            )
            .clickable { onStarClicked(0f, true) }
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_star_filled),
            contentDescription = "Start Your Journey",
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.Center)
        )
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun EnhancedUnitHeader(
    modifier: Modifier = Modifier,
    data: UnitData = UnitData(),
    unitIndex: Int,
    onGuideBookClicked: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            data.color,
                            data.darkerColor
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = data.description,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        lineHeight = 20.sp
                    )
                }

                AnimatedContent(
                    targetState = false,
                    transitionSpec = {
                        scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut()
                    },
                    label = "guideBookButton"
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable { onGuideBookClicked() }
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.3f),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_notebook),
                            tint = Color.White,
                            contentDescription = "Guide Book"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedSectionTransition(
    nextSection: SectionData,
    onJumpToSection: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Divider with animated gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Next Adventure",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = nextSection.title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = nextSection.description,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onJumpToSection,
            modifier = Modifier
                .height(48.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.1f),
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Continue Journey",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "Continue",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}