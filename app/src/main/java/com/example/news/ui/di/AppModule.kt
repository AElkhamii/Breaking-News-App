package com.example.news.ui.di

import android.app.Application
import androidx.room.Room
import com.example.news.ui.RetrofitAPI.NewsAPI
import com.example.news.ui.db.ArticleDao
import com.example.news.ui.db.ArticleDataBase
import com.example.news.ui.repository.NewsRepository
import com.example.news.ui.repository.NewsRepositoryInterface
import com.example.news.ui.utilities.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /* API Instance */
    @Singleton
    @Provides
    fun proviseNewsAPI(): NewsAPI{
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val clint = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clint)
                .build()
                .create(NewsAPI::class.java)
    }

    /* Room Instance */
    @Singleton
    @Provides
    fun ProviderArticleDataBase(app:Application): ArticleDataBase{
        return Room.databaseBuilder(
            app.applicationContext,
            ArticleDataBase::class.java,
            "article_db.db"
        ).build()
    }

    @Singleton
    @Provides
    fun providerArticleDataBaseDAO(articleDataBase: ArticleDataBase):ArticleDao{
        return articleDataBase.getArticleDao()
    }

    /* ViewModel Provider */
    @Singleton
    @Provides
    fun providerNewsRepository(database: ArticleDao, api: NewsAPI): NewsRepositoryInterface{
        return NewsRepository(database,api)
    }
}