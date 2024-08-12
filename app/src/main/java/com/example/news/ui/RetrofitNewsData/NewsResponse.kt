package com.example.news.ui.RetrofitNewsData

/* Retrofit Step Number 5: Create classes to receive the API response into objects with the same attributes names  */
/* You can create this step by using Kotlin Data class file from json plugin  */

/* This will be result return type of the function that will call a request from the API */
data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)