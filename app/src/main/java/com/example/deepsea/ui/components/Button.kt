package com.example.deepsea.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepsea.ui.theme.DeepSeaTheme
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import com.example.deepsea.ui.theme.FeatherGreen
import com.example.deepsea.ui.theme.FeatherGreenDark
import com.example.deepsea.ui.theme.Swan
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Path
import com.example.deepsea.R
import com.example.deepsea.ui.theme.Gray
import com.example.deepsea.ui.theme.GrayDark
import kotlinx.coroutines.delay
import kotlin.io.path.moveTo

@Composable

fun DeepSeaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ButtonShape,
    border: BorderStroke? = null,
    backgroundGradient: List<Color> = DeepSeaTheme.colors.interactivePrimary,
    disabledBackgroundGradient: List<Color> = DeepSeaTheme.colors.interactiveSecondary,
    contentColor: Color = DeepSeaTheme.colors.textInteractive,
    disabledContentColor: Color = DeepSeaTheme.colors.textHelp,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    DeepSeaSurface(
        shape = shape,
        color = Color.Transparent,
        contentColor = if (enabled) contentColor else disabledContentColor,
        border = border,
        modifier = modifier
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = if (enabled) backgroundGradient else disabledBackgroundGradient
                )
            )
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.labelLarge
        ) {
            Row(
                Modifier
                    .defaultMinSize(
                        minWidth = ButtonDefaults.MinWidth,
                        minHeight = ButtonDefaults.MinHeight
                    )
                    .indication(interactionSource, ripple())
                    .padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Composable
fun DeepSeaFAButton(
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFB2DFDB),
    contentColor: Color = Color.White,
    menuBackgroundColor: Color = Color(0xFF0A1929).copy(alpha = 0.95f),
    menuTextColor: Color = Color.White,
    menuWidth: Int = 200,
    iconOptions: List<Pair<String, ImageVector>> = emptyList(),
    onItemClick: (String) -> Unit = {}
) {
    var showMoreOptions by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .offset(y = (-20).dp)
    ) {
        FloatingActionButton(
            onClick = { showMoreOptions = !showMoreOptions },
            shape = RoundedCornerShape(
                topStart = 30.dp,
                bottomStart = 30.dp,
                topEnd = 30.dp
            ),
            containerColor = containerColor,
            contentColor = contentColor
        ) {
            // Animate icon rotation on click
            val rotationAngle by animateFloatAsState(
                targetValue = if (showMoreOptions) 45f else 0f,
                label = "rotationAngle"
            )

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "More options",
                modifier = Modifier.graphicsLayer(rotationZ = rotationAngle)
            )
        }

        // Dropdown menu
        DropdownMenu(
            expanded = showMoreOptions,
            onDismissRequest = { showMoreOptions = false },
            modifier = Modifier
                .background(menuBackgroundColor)
                .width(menuWidth.dp)
        ) {
            // Use provided options or default ones if empty
            val additionalOptions = if (iconOptions.isEmpty()) {
                listOf(
                    "Explore" to Icons.Default.Search,
                    "Favorites" to Icons.Default.Favorite,
                    "Settings" to Icons.Default.Settings,
                    "Help" to Icons.Default.Info

                )
            } else {
                iconOptions
            }

            additionalOptions.forEach { (title, icon) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = title,
                            color = menuTextColor
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor
                        )
                    },
                    onClick = {
                        onItemClick(title)
                        showMoreOptions = false
                    }
                )
            }
        }
    }
}


private val ButtonShape = RoundedCornerShape(percent = 50)

@Preview("default", "round")
@Preview("dark theme", "round", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "round", fontScale = 2f)
@Composable
private fun ButtonPreview() {
    DeepSeaTheme {
        DeepSeaButton(onClick = {}) {
            Text(text = "Demo")
        }
    }
}

@Preview("default", "rectangle")
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "rectangle", fontScale = 2f)
@Composable
private fun RectangleButtonPreview() {
    DeepSeaTheme {
        DeepSeaButton(
            onClick = {}, shape = RectangleShape
        ) {
            Text(text = "Demo")
        }
    }
}

@Composable
fun ImageButton(
    image: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(5.dp)
            .heightIn(min = 48.dp)
            .widthIn(min = 120.dp),
    ) {

            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontSize = 16.sp)

    }
}


@Composable
fun SelectableStarButton(
    colorMain: Color = FeatherGreen,
    colorDark : Color = FeatherGreenDark,
    onStarClicked: (coordinateInRoot: Float, isInteractive : Boolean) -> Unit,
    isInitial: Boolean = false,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    var isClicked by remember {
        mutableStateOf(false)
    }
    var positionInRoot by remember {
        mutableFloatStateOf(0f)
    }
    val animatedTranslation by animateFloatAsState(
        targetValue = if (isClicked) 0f else -20f
    )
    val textMeasurer = rememberTextMeasurer()
    val labelTranslation by rememberInfiniteTransition().animateFloat(
        initialValue = -40f,
        targetValue = -60f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "label"
    )
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .onGloballyPositioned {
                positionInRoot = it.positionInRoot().y
            }
            .drawWithCache {
                val text = if (isInitial) "START" else "JUMP HERE?"
                val textStyle = TextStyle(
                    color = colorMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                val textSize = textMeasurer.measure(
                    text = text,
                    style = textStyle
                ).size
                val padding = 18.dp.toPx()
                val rectSize =
                    Size(padding + textSize.width + padding, padding + textSize.height + padding)
                val totalTranslation = labelTranslation.dp.toPx()

                val indicatorSize = Size(width = 8.dp.toPx(), 8.dp.toPx())

                val textPaint = Paint()
                    .asFrameworkPaint()
                    .apply {
                        isAntiAlias = true
                        this.isFakeBoldText = true
                        this.color = colorMain.toArgb()
                        this.textSize = 18.sp.toPx()
                    }
                onDrawWithContent {
                    drawContent()
                    translate(top = totalTranslation) {
                        //box
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(x = size.center.x - rectSize.width / 2f, 0f),
                            size = rectSize,
                            cornerRadius = CornerRadius(x = 10.dp.toPx(), y = 10.dp.toPx())
                        )
                        //box border
                        drawRoundRect(
                            color = Swan,
                            topLeft = Offset(x = size.center.x - rectSize.width / 2f, 0f),
                            size = rectSize,
                            cornerRadius = CornerRadius(x = 10.dp.toPx(), y = 10.dp.toPx()),
                            style = Stroke(width = 1.5.dp.toPx())
                        )

                        //text
                        drawContext.canvas.nativeCanvas.apply {
                            this.drawText(
                                text,
                                size.center.x - textSize.width / 2f,
                                padding * 2f,
                                textPaint
                            )
                        }
                        //little rectangle at the bottom
                        val indicatorPath = Path().apply {
                            moveTo(
                                x = size.center.x - indicatorSize.width / 2f,
                                y = rectSize.height
                            )
                            lineTo(x = size.center.x, y = rectSize.height + indicatorSize.height)
                            lineTo(
                                x = size.center.x + indicatorSize.width / 2f,
                                y = rectSize.height
                            )
                        }
                        drawPath(
                            path = indicatorPath,
                            color = Color.White,
                            style = Fill
                        )
                        //indicator border
                        drawPath(
                            path = indicatorPath,
                            color = Swan,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
                }
            }
            .border(color = Swan, width = 6.dp, shape = CircleShape)
            .graphicsLayer {
                transformOrigin = TransformOrigin(0.5f, 0.5f)
                rotationX = 30f
            }
            .padding(12.dp)
            .background(color = colorDark, shape = CircleShape)

    ) {
        Image(
            modifier = Modifier
                .width(72.dp)
                .graphicsLayer {
                    translationY = animatedTranslation
                }
                .padding(0.dp)
                .background(color = colorMain, shape = CircleShape)
                .padding(20.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        scope.launch {
                            isClicked = true
                            delay(80)
                            onStarClicked(positionInRoot, true)
                            isClicked = false
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            isClicked = true
                        }, onDragEnd = {
                            isClicked = false
                            onStarClicked(positionInRoot, true)
                        }
                    ) { _, _ -> }
                },
            painter = painterResource(id = if (isInitial) R.drawable.ic_star else R.drawable.ic_skip),
            colorFilter = ColorFilter.tint(color = Color.White),
            contentDescription = "star"
        )
    }
}

@Composable
fun StarButton(
    onStarClicked: (coordinateInRoot: Float, isInteractive : Boolean) -> Unit,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    //val isClicked by interactionSource.collectIsPressedAsState()
    var isClicked by remember {
        mutableStateOf(false)
    }
    val animatedTranslation by animateFloatAsState(
        targetValue = if (isClicked) 0f else -23f,
        animationSpec = tween(80)
    )
    var positionInRoot by remember {
        mutableFloatStateOf(0f)
    }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .animateContentSize()
            .onGloballyPositioned {
                positionInRoot = it.positionInRoot().y
            }
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    rotationX = 30f
                }
                .padding(12.dp)
                .background(color = GrayDark, shape = CircleShape)
        ) {
            Image(
                modifier = Modifier
                    .width(72.dp)
                    .graphicsLayer {
                        translationY = animatedTranslation
                    }
                    .padding(0.dp)
                    .background(color = Gray, shape = CircleShape)
                    .padding(20.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            scope.launch {
                                isClicked = true
                                delay(80)
                                onStarClicked(positionInRoot, false)
                                isClicked = false
                            }
                        }
                    }.pointerInput(Unit) {
                        detectDragGesturesAfterLongPress (
                            onDragStart = {
                                isClicked = true
                            }, onDragEnd = {
                                isClicked = false
                                onStarClicked(positionInRoot, false)
                            }
                        ) { _,_-> }
                    }

                ,
                painter = painterResource(id = R.drawable.ic_star),
                colorFilter = ColorFilter.tint(color = GrayDark),
                contentDescription = "star"
            )
        }
    }
}
