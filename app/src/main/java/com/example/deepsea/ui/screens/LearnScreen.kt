package com.example.deepsea.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizImageScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E3F)), // Màu nền tối giống trong ảnh
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Phần trên cùng: Khung bao bọc mũi tên quay lại và "Single Player"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFF2A2A5A)) // Thanh tiêu đề màu tối hơn
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Nút mũi tên quay lại
                IconButton(
                    onClick = { /* Xử lý quay lại */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Khung "Single Player"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF3A3A6A))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Single Player",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Phần câu hỏi: Khung bao bọc "Question 1/10" và hai mũi tên
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color(0xFF2A2A5A))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Khung "Question 1/10"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF3A3A6A))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Question 1/10",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Hai nút mũi tên trái/phải
                Row {
                    IconButton(
                        onClick = { /* Xử lý mũi tên trái */ },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowLeft,
                            contentDescription = "Previous",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { /* Xử lý mũi tên phải */ },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowRight,
                            contentDescription = "Next",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Phần hình ảnh chính giữa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder cho hình ảnh
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Gray)
            ) {
                Text(
                    text = "Image Placeholder",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Phần dưới cùng (giữ nguyên)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFF1E1E3F))
        )
    }
}

@Preview(showBackground = true, apiLevel = 35)
@Composable
fun QuizImageScreenPreview() {
    QuizImageScreen()
}