package com.example.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.news.R
import com.example.news.databinding.FragmentArticlesBinding
import com.example.news.databinding.FragmentSavedNewsBinding
import com.example.news.ui.NewsActivity
import com.example.news.ui.userInterface.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticlesFragment: Fragment() {
    // Declare the binding property
    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NewsViewModel

    /* ArticlesFragmentArgs is a class that navigation component created for us when rebuild the project
     * this variable will be used to get the selected article data */
    val args: ArticlesFragmentArgs by navArgs() //(by) delegation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /****** ViewModel ******/
        // Safe cast to NewsActivity and access the ViewModel
        activity?.let {
            viewModel = (it as NewsActivity).viewModel
        } ?: throw IllegalStateException("Activity cannot be null")
        // You can now use the viewModel safely

        /****** open article on click ******/
        /* Get the current article, the article that was passed as arguments to this article fragment transition  */
        val article = args.article

        /* set the web view with the article url */
        binding.wvArticle.apply {
            /* to make sure that the page will always load inside this web view and don't load in the standard browser of the phone */
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        /****** ROOM ******/
        binding.fabFavorit.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view,"Article saved successfully",Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottomNavigationView).show()
        }
    }
}