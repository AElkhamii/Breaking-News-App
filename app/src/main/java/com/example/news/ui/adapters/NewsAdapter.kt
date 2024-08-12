package com.example.news.ui.adapters

import android.provider.MediaStore.Images
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.R
import com.example.news.ui.RetrofitNewsData.Article

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    /****** ViewHolder ******/
    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    /****** DiffUtil ******/

    /* Normally if you have recyclerview adaptor, you will pass a list of articles to constructor of NewsAdaptor.
     * Everytime you want to add an article then you add it to the list and call adaptor.notify data set changed.
     * That is very inefficient because by using notify data set changed,
     * the recycler view adapter will always update its hole items even the items that did not changed.
     * To solve this problem we will use DiffUtil   */
    /* DiffUtil: Calculate the differences between two lists and enable us to update only those items that were different
     * Another advantage of that is that is actually happen in background so we don't block main thread with that */
    private val differCallback = object: DiffUtil.ItemCallback<Article>() {
        /* This function check if the two articles are the same or not (new article vs old article) */
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            /* Because each item has a unique url (it is like id, but id is local to our data base), so e ill check if the articles have the same id or not */
            return oldItem.url == newItem.url
        }

        /* This function will compare old object content with new object content */
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    /* Async list differ is the tool that will take our two lists and compares them and calculates the differences
     * and it ill run in background because it is asynchronous   */
    val differ = AsyncListDiffer(this@NewsAdapter,differCallback)

    /****** Adaptor Implementation ******/

    /* Every time new data is added to the list, the View holder inflate new layout (item_article_preview)
     * to add article to RecyclerView */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        /* Inflate the view you are going to set inside RecyclerView */
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview,parent,false)
        /* Set the inflated View to ArticleViewHolder which will present the inflated item_article_preview on the list */
        return ArticleViewHolder(view)
    }

    /* This function is used to bind the data to layout (item_article_preview) items
     * it will take the data from the list and set it to the corresponding view */
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        /* Get the current article */
        val article = differ.currentList[position]

        /* This contains the content each single item (item_article_preview) in the RecyclerView  */
        holder.itemView.apply {
            /* We will first load the image from our article *into* our image view using GLIDE */
            Glide.with(this).load(article.urlToImage).error(R.drawable.no_image).into(findViewById(R.id.ivArticleImage))

            /* Set text view values */
            findViewById<TextView>(R.id.tvSource).text = article.source?.name
            findViewById<TextView>(R.id.tvTitle).text = article.title
            findViewById<TextView>(R.id.tvDescreption).text = article.description
            findViewById<TextView>(R.id.tvPublishedAt).text = article.publishedAt

            /* To call setOnItemClickListener when click on an item */
            setOnClickListener {
                Log.d("NewsAdapter", "Article clicked: ${article.title}")
               /* ? means Check if onItemClickListener not equal to null if true continue executing let scope */
                onItemClickListener?.let {it(article)} /* call onItemClickListener (it) with current article */
            }
        }
    }

    /* This function return number of articles in the recycler view  */
    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    /****** Item Click Listener ******/

    /* we want to be able to click on the item article to open web view that shows our article  */
    /* We will pass current Article when we click on an item to that lambda function
     * So we will be able to open the correct web view page */
    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(Listener:(Article) -> Unit){
        onItemClickListener = Listener
    }
}