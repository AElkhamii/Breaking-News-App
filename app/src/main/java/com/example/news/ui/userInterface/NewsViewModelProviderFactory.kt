package com.example.news.ui.userInterface

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.news.ui.repository.NewsRepository

class NewsViewModelProviderFactory(
    val app: Application,
    val newsRepository: NewsRepository
): ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        /* We will return a new instance of our News viewmodel and pass our news Repository, after that cast that class as T */
//        return NewsViewModel(app,newsRepository) as T
//    }
}