package com.example.news.ui.repository

import androidx.lifecycle.LiveData
import com.example.news.ui.RetrofitAPI.NewsAPI
import com.example.news.ui.RetrofitAPI.RetrofitInstance
import com.example.news.ui.RetrofitNewsData.Article
import com.example.news.ui.RetrofitNewsData.NewsResponse
import com.example.news.ui.db.ArticleDao
import com.example.news.ui.db.ArticleDataBase
import retrofit2.Response
import java.util.Locale.IsoCountryCode
import javax.inject.Inject

/* We need to create a Repository class to handel where are you going to get the data to present in ui from
 * 1- Get data when the application is not connected to internet from the database
 * 2- Get the data when the application is connected to the internet from the data base */

/* This NewsRepository will have database as a contractor parameters to access database functions our data base
 * This NewsRepository will also need to access the API but we can get that from RetrofitInstance class by calling api
 * we will not give retrofitAPI to NewsRepository as a parameter because we will access it directly from RetrofitInstance class
 */

class NewsRepository @Inject constructor(private val db:ArticleDao, private val api:NewsAPI): NewsRepositoryInterface{
    /****** API ******/
    /* This function will call the api instance with selected country and selected page number
     * To get the news from url with selected filters */
    override suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> =
        api.getBreakingNews(countryCode,pageNumber)

    /* This function will call the api instance with selected country and selected page number
     * To search for the news from url with selected filters */
    override suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponse> =
        api.searchForNews(searchQuery,pageNumber)

    override suspend fun upsert(article: Article): Long =
        db.upsert(article)

    override fun getSavedNews(): LiveData<List<Article>> =
        db.getAllArticles()

    override suspend fun deleteArticle(article: Article) =
        db.deleteArticle(article)
}
