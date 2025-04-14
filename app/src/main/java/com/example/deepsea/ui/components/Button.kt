package com.example.deepsea.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector

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
