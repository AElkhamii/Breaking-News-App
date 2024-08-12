package com.example.news.ui.RetrofitAPI

import com.example.news.ui.utilities.Constants
import com.example.news.ui.utilities.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    /* we will create a singleton pattern to prevent creating retrofit instance for each time we use retrofit */
    companion object{
        /* lazy means: once you need it, it will initialize */
        private val retrofit by lazy {

            /* logs HTTP request and response data by using logging interceptor. */
            val logging = HttpLoggingInterceptor()
            /* Attach it to a retrofit object to be able to see which request we are actually making and what the responses are */
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)/* To see the body of our response  */

            /* build a clint to present the logs */
            val clint = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            /* Retrofit instance */
            /* Retrofit Step Number 3: Create the builder */
            Retrofit.Builder()
                .baseUrl(BASE_URL)                                  /* Retrofit Step Number 1: Set BASE_URL to get data from this URL */
                .addConverterFactory(GsonConverterFactory.create()) /* Retrofit Step Number 4: select converter type (GSON) */
                .client(clint)                                      /* Clint to present the response result in the logs*/
                .build()                                      /* Build the builder*/
        }

        /* API Instance
         * Retrofit Step Number 2: Generate Interface by using retrofit object */
        /* lazy means: only initialize what inside lazy curly bracts only just once */
        val api by lazy {
            /* Here we used retrofit object that created above to fill the NewsAPI body functions interface */
            /* :: Reflection */
            retrofit.create(NewsAPI::class.java)
        }

        /* The last step (Step Number 6) is to call api.getBreakingNews() after that make call back to get the response from server */
    }
}