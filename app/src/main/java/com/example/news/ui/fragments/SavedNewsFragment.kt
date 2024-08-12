package com.example.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.databinding.FragmentSavedNewsBinding
import com.example.news.databinding.FragmentSearchNewsBinding
import com.example.news.ui.NewsActivity
import com.example.news.ui.adapters.NewsAdapter
import com.example.news.ui.userInterface.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedNewsFragment: Fragment() {
    // Declare the binding property
    private var _binding: FragmentSavedNewsBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    val TAG = "SavedNews Fragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
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
                R.id.action_savedNewsFragment_to_articlesFragment,
                bundle
            )
        }

        /****** ROOM ******/
        /* Swipe and delete articles */
        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            /* We will not use this function because we don't have functionality we want to adapt with that */
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                /* get the position of the item that we want to delete */
                val position = viewHolder.adapterPosition
                /* get the corresponding article that we want to delete from the data base */
                val article = newsAdapter.differ.currentList[position]
                /* delete the article */
                viewModel.deleteArticle(article)
                Snackbar.make(view,"Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAnchorView(R.id.bottomNavigationView)
                    /* action to that snack bar that is executed when we click on that undo button */
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        /* we need to create an item touch helper and pass our item touch helper callback */
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }


        /* Whenever data in DB changes the this observer gets called and passes us the new list of articles */
        viewModel.getSaveNews().observe(viewLifecycleOwner, Observer {articles ->
            /* we will update our recycler view
             * and then our list differ will automatically calulate the differences of the new list and the old list and update our RV accordingly*/
            newsAdapter.differ.submitList(articles)
        })
    }

    private fun setUpRecyclerView(){
        // Create adapter instant
        newsAdapter = NewsAdapter()
        // Inflate the layout for this fragment
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}