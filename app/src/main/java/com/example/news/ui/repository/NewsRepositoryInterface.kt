package com.example.news.ui.repository

import androidx.lifecycle.LiveData
import com.example.news.ui.RetrofitAPI.RetrofitInstance
import com.example.news.ui.RetrofitNewsData.Article
import com.example.news.ui.RetrofitNewsData.NewsResponse
import dagger.Provides
import retrofit2.Response


interface NewsRepositoryInterface {
    /****** API ******/
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse>
    suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponse>

    /****** ROOM ******/
    suspend fun upsert(article: Article): Long
    fun getSavedNews(): LiveData<List<Article>>
    suspend fun deleteArticle(article: Article): Unit
}