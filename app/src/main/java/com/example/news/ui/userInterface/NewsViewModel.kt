package com.example.news.ui.userInterface

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.ui.NewsApplication
import com.example.news.ui.RetrofitNewsData.Article
import com.example.news.ui.RetrofitNewsData.NewsResponse
import com.example.news.ui.repository.NewsRepository
import com.example.news.ui.repository.NewsRepositoryInterface
import com.example.news.ui.utilities.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response
import javax.inject.Inject

/* we cannot use constructor parameters by default for our own view models,
 * if we want to do that here we need to create what is called view model provider factory.
 * to define how our view model should be created  */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepositoryInterface,
    private val app: Application,  // to reference an application in the constructor
): AndroidViewModel(app) {

    /* create live data to receive updated list from the API */
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    /* create live data to receive search result from the API */
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        /* Request for breaking news */
        getBreakingNews("us")
    }

    /****** API ******/
    /* We does mpt need to set breakingNewsPage as a parameter because we will handel that from var breakingNewsPage */
    /* since we use suspend function inside our repository, that means we have to call getBreakingNews function
     * in a coroutine and we won't make this function is suspend function because we won't to propagate that function
     * to our fragment and we would need to start the coroutine in the fragment, so we need to start coroutine in this function */
    /* Ro start a coroutine in the ViewModel we will use viewModelScope to make this coroutine alive as long as the viewmodel alive */
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
        /* Before we make actual network call we need to omit th loading state to our life data
         * because we now know that we about to make a network call so we should omit that loading state
         * so our fragment can handel that*/
//        breakingNews.postValue(Resource.Loading())

        /* Make our actual network response */
//        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)

        /* pass the function that will handel the response to MutableLiveData (breakingNews) */
//        breakingNews.postValue(handelBreakingNewsResponse(response))
    }

    /* Search for news */
    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
//        searchNews.postValue(Resource.Loading())
//        val response = newsRepository.searchNews(searchQuery,searchPage)
//        searchNews.postValue(handelSearchNewsResponse(response))
    }

    /* Function to handel news response */
    private fun handelBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++                                   // Increase page number for each request
                if(breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                }else{
                    val oldArticles = breakingNewsResponse?.articles // old articles
                    val newArticles = resultResponse.articles        // new articles
                    oldArticles?.addAll(newArticles)                 // add new articles to the old one
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    /* Function to handel search response */
    private fun handelSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                searchPage++                                        // Increase page number for each request
                if(searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                }else{
                    val oldArticles = searchNewsResponse?.articles   // old articles
                    val newArticles = resultResponse.articles        // new articles
                    oldArticles?.addAll(newArticles)                 // add new articles to the old one
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    /****** ROOM ******/
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    /* it is not a suspend function so we don't need to start a coroutine for that
     * Instead we will just observe on this function from our fragment. so we directly notified about changes in database */
    fun getSaveNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }



    /****** Safe Internet Checking ******/

    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
                breakingNews.postValue(handelBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = newsRepository.searchNews(searchQuery,searchPage)
                searchNews.postValue(handelSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network failure"))
                else -> searchNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }


    /****** Has internet connection function ******/
    private fun hasInternetConnection(): Boolean {
        /* Reference to our connectivity manger */
        /* connectivityManger will be used to detect if the user is currently connected to the internet or not  */
        /* kotlin does not know this is a connectivity manger, because we get system function and that return an object so that could be anything.
         * so we cast this variable as ConnectivityManager */
        val connectivityManger = getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        /* Check if ths SDK version is less than or equal to API version number 26 */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activityNetwork = connectivityManger.activeNetwork ?: return false
            val capabilities = connectivityManger.getNetworkCapabilities(activityNetwork) ?: return false

            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManger.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI ->  true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}