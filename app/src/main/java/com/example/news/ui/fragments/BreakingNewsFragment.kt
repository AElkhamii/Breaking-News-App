package com.example.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.ui.NewsActivity
import com.example.news.ui.adapters.NewsAdapter
import com.example.news.ui.userInterface.NewsViewModel
import com.example.news.ui.utilities.Constants.Companion.QUERY_PAGE_SIZE
import com.example.news.ui.utilities.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreakingNewsFragment: Fragment() {
    var rootView: View? = null

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    val TAG = "BreakingNews Fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_breaking_news, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /****** ViewModel ******/
        // Safe cast to NewsActivity and access the ViewModel
        activity?.let {
            viewModel = (it as NewsActivity).viewModel
        } ?: throw IllegalStateException("Activity cannot be null")
        // You can now use the viewModel safely

        /****** Setup the recycler view ******/
        setUpRecyclerView()

        /****** When click on an article ******/
        /* we will take this article and put it into a bundle then attach this bundle to our navigation components
        * so that the navigation component will handel the transition for us and pass the arguments to our article fragment */
        newsAdapter.setOnItemClickListener {article ->
            Log.d(TAG, "Article clicked: $article")
            val bundle = Bundle().apply {
                putSerializable("article",article)
            }
            /* Fragment transition */
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articlesFragment,
                bundle
            )
        }


        /****** Breaking news handler ******/
        /* we basically subscribe to all the changes regardless their life data
         * so when ever we are getting new breaking news then this observer will be called
         * and we can handel that new response in our fragment and update our recyclerView */
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults/ QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        /* the purpose of next operation is just to reset the padding so that
                         * the progressBar actually has its oun space and is not overlapping our recyclerview */
                        if(isLastPage){
                            rootView?.findViewById<RecyclerView>(R.id.rvBreakingNews)?.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {message ->
                        Log.e(TAG,"An error occurred: $message")
                        Toast.makeText(activity,"An error occurred: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }


    /****** Pagination ******/
    var isLoading = false
    var isLastPage = false //to stop paginating
    var isScrolling = false

    /* After that pass scrollListener anonymous object to the setUpRecyclerView function */
    val scrollListener =  object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            /* check if we are currently scrolling */
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            /* there is no default mechanism to tell us we whether we scroll until the bottom or not
             * so we need to actually make some calculation with the layout manger of our recyclerview */
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            /* With  those three variables we can make some calculation to check if we scroll until the bottom of the recyclerView */

            /* we will also need also some boolean */
            val isNotLoadingAndIsNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount // we wil know if the last item is visible or not
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            /* with all of those booleans we can determine if we should paginate or not */
            val shouldPaginate = isNotLoadingAndIsNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }


    /****** Private functions ******/
    /* Function to set recycler view to newsAdapter and LinearLayoutManager */
    private fun setUpRecyclerView(){
        // Create adapter instant
        newsAdapter = NewsAdapter()
        // Inflate the layout for this fragment
        rootView?.findViewById<RecyclerView>(R.id.rvBreakingNews)?.apply{
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    private fun hideProgressBar(){
        rootView?.findViewById<ProgressBar>(R.id.paginationProgressBar)?.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar(){
        rootView?.findViewById<ProgressBar>(R.id.paginationProgressBar)?.visibility = View.VISIBLE
        isLoading = true
    }

}