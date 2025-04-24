package com.example.deepsea.data.model
import androidx.annotation.DrawableRes
import com.example.deepsea.R

enum class SurveyOptionType(val displayName: String,@DrawableRes val flagResId: Int) {
    FRIENDS("Friends", R.drawable.ic_friends),
    TV("TV", R.drawable.ic_tv),
    TIKTOK("TikTok", R.drawable.ic_tiktok),
    NEWS("News", R.drawable.ic_news),
    YOUTUBE("Youtube", R.drawable.ic_youtube),
    SOCIAL("Social", R.drawable.ic_social),
    OTHER("Other", R.drawable.ic_other)
}
