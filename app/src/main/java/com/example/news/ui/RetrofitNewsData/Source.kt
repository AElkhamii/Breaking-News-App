package com.example.news.ui.RetrofitNewsData

import java.io.Serializable

/* Retrofit Step Number 5: Create classes to receive the API response into objects with the same attributes names  */
/* You can create this step by using Kotlin Data class file from json plugin  */
data class Source(
    val id: String?,
    val name: String?
) : Serializable