package com.example.financas.ui.theme

// Set of Material typography styles to start with
import androidx.compose.material.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    h1 = Typography().h1.copy(fontSize = 30.sp, fontWeight = FontWeight.Bold),
    body1 = Typography().body1.copy(fontSize = 16.sp),
    button = Typography().button.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    caption = Typography().caption.copy(fontSize = 12.sp)
)
