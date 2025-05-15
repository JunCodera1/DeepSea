package com.example.deepsea.ui.screens.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.friend.FriendSuggestion
import com.example.deepsea.ui.viewmodel.user.FriendSuggestionViewModel
import com.example.deepsea.ui.viewmodel.user.FriendSuggestionViewModelFactory

import com.example.deepsea.utils.SessionManager

@Composable
fun FriendSuggestionCard(
    sessionManager: SessionManager,
    viewModel: FriendSuggestionViewModel = viewModel(
        factory = FriendSuggestionViewModelFactory(
            RetrofitClient.friendSuggestionService,
            sessionManager
        )
    )
) {
    val suggestions = viewModel.suggestions
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val hasFetchedSuggestions = viewModel.hasFetchedSuggestions.value

    // Load suggestions when the component is first displayed
    LaunchedEffect(Unit) {
        if (!hasFetchedSuggestions) {
            viewModel.loadFriendSuggestions()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Psychology,
                        contentDescription = "Friend suggestions",
                        tint = Color(0xFF4DB6FF)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "People you might know",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    FriendSuggestionCard(sessionManager = sessionManager)
                }

                TextButton(onClick = { viewModel.generateFriendSuggestions() }) {
                    Text(
                        text = "Refresh",
                        color = Color(0xFF4DB6FF),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4DB6FF))
                }
            }
            // Show error message if any
            else if (error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = error ?: "An error occurred",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = {
                            viewModel.clearError()
                            viewModel.loadFriendSuggestions()
                        }) {
                            Text("Retry")
                        }
                    }
                }
            }
            // Show no suggestions message
            else if (suggestions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "No suggestions",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No suggestions available right now",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.generateFriendSuggestions() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6FF))
                        ) {
                            Text("Find Friends")
                        }
                    }
                }
            }
            // Show friend suggestions
            else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(suggestions) { suggestion ->
                        FriendSuggestionItem(
                            suggestion = suggestion,
                            onFollow = { viewModel.followSuggestion(suggestion.id) },
                            onDismiss = { viewModel.dismissSuggestion(suggestion.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendSuggestionItem(
    suggestion: FriendSuggestion,
    onFollow: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(220.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile image
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (suggestion.suggestedUserAvatarUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(suggestion.suggestedUserAvatarUrl),
                        contentDescription = "Profile picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile picture",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Name and username
            Text(
                text = suggestion.suggestedUserName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "@${suggestion.suggestedUsername}",
                color = Color.Gray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Shared languages
            if (suggestion.sharedLanguages.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    suggestion.sharedLanguages.take(2).forEach { language ->
                        Image(
                            painter = painterResource(id = language.flagResId),
                            contentDescription = language.displayName,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(horizontal = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Suggestion reason
            Text(
                text = suggestion.suggestionReason,
                fontSize = 12.sp,
                color = Color(0xFF4DB6FF),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Dismiss button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFFDEDE), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Follow button
                IconButton(
                    onClick = onFollow,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFD6F5D6), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Follow",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}