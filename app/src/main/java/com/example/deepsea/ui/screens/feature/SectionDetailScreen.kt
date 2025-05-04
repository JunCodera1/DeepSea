package com.example.deepsea.ui.screens.feature

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deepsea.ui.components.SectionData
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.input.pointer.pointerInput
import com.example.deepsea.ui.components.TopBar


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SectionDetailScreen(
    section: SectionData,
    onContinueClick: () -> Unit,
    onJumpToSection: (sectionIndex: Int, unitIndex: Int) -> Unit,
    onSeeDetailsClick: () -> Unit,
    navController: NavController,
    sections: List<SectionData> = listOf(section)
) {
    val coroutineScope = rememberCoroutineScope()

    // Tìm index của section hiện tại trong danh sách sections
    val initialPage = sections.indexOf(section).takeIf { it >= 0 } ?: 0

    // Thiết lập pager state để vuốt ngang giữa các section
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { sections.size }
    )

    // Thiết lập UI với Scaffold
    Scaffold(
        topBar = {
            SectionTopBar(
                section = sections[pagerState.currentPage],
                navController = navController,
                onExpandClick = onContinueClick,
                isExpanded = true
            )
        }
    ) { paddingValues ->
        // Horizontal Pager cho phép vuốt ngang giữa các section
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            val currentSection = sections.getOrNull(page) ?: section

            // Content với phần đệm để không che khuất TopBar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(currentSection.color)
            ) {
                // Mascot image at the top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Sử dụng trường image từ currentSection
                    Image(
                        painter = painterResource(id = currentSection.image),
                        contentDescription = "Section Image",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )

                    // Add decorations around
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.7f))
                            .align(Alignment.TopEnd)
                            .offset(x = (-50).dp, y = 70.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f))
                            .align(Alignment.CenterStart)
                            .offset(x = 70.dp, y = (-30).dp)
                    )
                }

                // Content card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = currentSection.color.copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    border = null
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Section title
                        Text(
                            text = currentSection.title,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        // Section description
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentSection.description,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress indicator - hiển thị trang hiện tại trong tổng số section
                        Text(
                            text = "${page + 1} / ${sections.size} SECTIONS",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((page + 1).toFloat() / sections.size)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Unit count indicator
                        Text(
                            text = "${currentSection.units.size} UNITS",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // "See Details" button
                        Button(
                            onClick = onSeeDetailsClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentSection.darkerColor
                            ),
                            shape = RoundedCornerShape(32.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${currentSection.level} • SEE DETAILS",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // "Continue" button
                        Button(
                            onClick = {
                                onJumpToSection(page, 0)
                                      },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(32.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "JUMP HERE",
                                color = currentSection.color,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Pagination dots
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(sections.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == pagerState.currentPage) Color.White
                                            else Color.White.copy(alpha = 0.5f)
                                        )
                                        .pointerInput(Unit) {
                                            detectTapGestures {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(index)
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTopBar(
    section: SectionData,
    navController: NavController,
    onExpandClick: () -> Unit,
    isExpanded: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(section.color)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back button
        Button(
            onClick = { navController.navigate("home") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text("Back", color = Color.White)
        }

        // Title
        Text(
            text = section.title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        // Expand/Collapse or Action Button
        Button(
            onClick = onExpandClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(if (isExpanded) "Collapse" else "Expand", color = Color.White)
        }
    }
}