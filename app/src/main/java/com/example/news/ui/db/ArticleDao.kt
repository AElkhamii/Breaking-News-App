package com.example.news.ui.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.news.ui.RetrofitNewsData.Article

/* Room Step Number 2: create Data Access Object Interface
 * which is responsible on doing all queries types on the data base that you create */
@Dao
interface ArticleDao {

    /* Insert favourite article in the data base */
    /* onConflict strategy determine what happen if that article that e ant to insert that database already exist in our data base
     * in that case we want to replace that article */
    /* pass the article that you ant to save into the data base and return a long which is the ID that was inserted*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    /* Query method to get all available articles in the data base */
    /* This function is not a suspend function because it will return LiveData object and that doesn't ok with suspend functions */
    /* LiveData is a class of those android arichitecture component that enables our fragments to subscribe to changes of that LiveData\
     * whenever that data in our DataBase changes then the life data will notify all of its observers so our fragments about those changes
     * so they can update the recycler view in our case */
    /* Whenever an article inside of that list changes then this LiveData wil notify all of its observers that subscribed to changes of that LiveData*/
    @Query("SELECT * FROM articles_table")
    fun getAllArticles(): LiveData<List<Article>>

    /* Delete certain article from the data base */
    @Delete
    suspend fun deleteArticle(article: Article)
}