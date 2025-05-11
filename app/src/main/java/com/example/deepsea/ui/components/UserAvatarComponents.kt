package com.example.deepsea.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.deepsea.ui.viewmodel.auth.AvatarUploadState

/**
 * A composable that displays a user avatar with different states (loading, error, success)
 *
 * @param avatarUrl The URL of the avatar image to display
 * @param size The size of the avatar
 * @param onClick Optional click handler for the avatar
 */
@Composable
fun UserAvatar(
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    size: Int = 60,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val imageRequest = ImageRequest.Builder(context)
        .data(avatarUrl)
        .crossfade(true)
        .transformations(CircleCropTransformation())
        .build()

    val painter = rememberAsyncImagePainter(imageRequest)

    val clickableModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = clickableModifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size((size / 2).dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
            is AsyncImagePainter.State.Error -> {
                // Default avatar when image fails to load
                DefaultAvatar()
            }
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = "User avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                // Default placeholder
                DefaultAvatar()
            }
        }
    }
}

@Composable
private fun DefaultAvatar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Default avatar",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * A composable that handles avatar selection with upload state
 *
 * @param avatarUrl The URL of the avatar image
 * @param uploadState The current state of avatar upload
 * @param onPickImage Callback for when the user wants to pick an image
 */
@Composable
fun AvatarSelector(
    avatarUrl: String?,
    uploadState: AvatarUploadState,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            when (uploadState) {
                is AvatarUploadState.Loading -> {
                    // Show a loading indicator overlaid on the avatar
                    Box {
                        UserAvatar(
                            avatarUrl = avatarUrl,
                            size = 100,
                            onClick = null
                        )

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                is AvatarUploadState.Error -> {
                    // Show avatar with option to retry
                    UserAvatar(
                        avatarUrl = avatarUrl,
                        size = 100,
                        onClick = onPickImage
                    )
                }

                else -> {
                    // Normal state - avatar with option to change
                    UserAvatar(
                        avatarUrl = avatarUrl,
                        size = 100,
                        onClick = onPickImage
                    )
                }
            }

            // Camera icon indicator for selection
            if (uploadState !is AvatarUploadState.Loading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.BottomEnd)
                        .clickable(onClick = onPickImage)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Change avatar",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Show upload state messages
        when (uploadState) {
            is AvatarUploadState.Error -> {
                Text(
                    text = uploadState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
            is AvatarUploadState.Loading -> {
                Text(
                    text = "Uploading image...",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            is AvatarUploadState.Success -> {
                Text(
                    text = "Image uploaded successfully",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            else -> {
                Text(
                    text = "Tap to select an avatar",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * A preview component for avatar selection in profile setup screens
 *
 * @param avatarUrl The URL of the avatar image
 * @param size The size of the avatar preview
 */
@Composable
fun AvatarPreview(
    avatarUrl: String?,
    size: Int = 120,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        UserAvatar(
            avatarUrl = avatarUrl,
            size = size
        )
    }
}