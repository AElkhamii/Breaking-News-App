package com.example.news.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.news.R
import com.example.news.ui.db.ArticleDataBase
import com.example.news.ui.repository.NewsRepository
import com.example.news.ui.userInterface.NewsViewModel
import com.example.news.ui.userInterface.NewsViewModelProviderFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /****** ViewModel ******/
//        /* Create a repository instance and create inside this instance an instance from the data base bu using invoke function inside ArticleDataBase */
//        val newsRepository = NewsRepository(ArticleDataBase(this))
//
//        /* Create a view model instance that will take newsRepository as a parameter */
//        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
//
//        /* Create a viewmodel instance by using ViewModelProvider and passing viewModelProviderFactory to it */
//        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)


        /****** Bottom Navigation View ******/
        /* To let bottom navigation buttons have access to switch between fragments by passing Default navigation host fragment to that bottom navigation bar */
        /* Remember, when you created each item in bottom navigation bar, you set the id of each item to same fragments id in the news_nav_graph */
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        supportFragmentManager.findFragmentById(R.id.newsNavHosFragment)?.let { containerFragment->
            bottomNavigationView.setupWithNavController(containerFragment.findNavController())
        }
    }
}