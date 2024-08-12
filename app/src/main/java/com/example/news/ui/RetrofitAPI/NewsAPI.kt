package com.example.news.ui.RetrofitAPI

import com.example.news.ui.RetrofitNewsData.NewsResponse
import com.example.news.ui.utilities.Constants
import com.example.news.ui.utilities.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale.IsoCountryCode

/* Retrofit Step Number 2: Create Interface to GET or POST data */
interface NewsAPI {

    /* This functions to get breaking news headlines for countries, categories, and singular publishers.
     * This is perfect for use with news tickers or anywhere you want to use live up-to-date news headlines. */
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        /* Fertilization Parameters */

        /* Filter the result query by country */
        @Query("country")
        countryCode: String = "us",
        /* Filter the result query by number of pages to get one page (20 articles) per request */
        @Query("page")
        pageNumber: Int = 1,
        /* To let the API news know who made that request */
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse> /* Return a response of type NewsResponse which contain a list of articles variable */


    /* This function used to search every article published by over 150,000 different sources large and small in the last 5 years.
     * This endpoint is ideal for news analysis and article discovery. */
    @GET("v2/everything")
    suspend fun searchForNews(
        /* Fertilization Parameters */

        /* Filter the result query by search string value */
        @Query("q")
        searchQuery: String,
        /* Filter the result query by number of pages to get one page (20 articles) per request */
        @Query("page")
        pageNumber: Int = 1,
        /* To let the API news know who made that request */
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse> /* Return a response of type NewsResponse which contain a list of articles variable */
}