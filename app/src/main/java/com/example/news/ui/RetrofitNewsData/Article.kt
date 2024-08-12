package com.example.news.ui.RetrofitNewsData

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/* Retrofit Step Number 5: Create classes to receive the API response into objects with the same attributes names  */
/* You can create this step by using Kotlin Data class file from json plugin  */

/* Room Step Number 1: Create a table */
@Entity(tableName = "articles_table") /* This annotation will tell the Android Studio that this article class is a table in our database */
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
): Serializable

/* we will serialize this class because it is not a primitive data type and
 * we need to pass this class between several fragments with the navigation components */