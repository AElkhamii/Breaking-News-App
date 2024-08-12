package com.example.news.ui.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.news.ui.RetrofitNewsData.Article

/* Room Step Number 3: create DataBase abstract class */
/* Data base class for room always need to be abstract, because it will implement abstract method and require to inherits from RoomDataBase */
@Database(
    entities = [Article::class], /* Number of tables: we have single table which is the Articles table */
    version = 1                  /* This is used to update data base later when some changes occur */
)
@TypeConverters(Converters::class) /* To tell our data base e are going to use type converters from this class */
abstract class ArticleDataBase: RoomDatabase() {
    /* DataBase create Step number 1:
     * Function to return Article Data Access Object
     * To give you access to the data base entities by using the queries that created inside ArticleDao */
    abstract fun getArticleDao(): ArticleDao

//    /* DataBase create Step number 2:
//     * create a Data Base using singleton pattern to prevent creating a new database instance each time we use the data base */
//    companion object{
//        /* Create an instance from Article DataBase which its class inherits from RoomDatabase() */
//        @Volatile /* This will make other threads can immediately see (notify) when a thread changes this instance */
//        private var instance: ArticleDataBase? = null /* the instance that we created inside the singleton will be able to use this object to access the data inside dfatabase */
//
//        /* we will use that to synchronize setting that instance so that we make sure there is only a single instance of our database at once*/
//        private val LOCK = Any()
//
//        /* This function will be called whenever we create an instance of the database then return the current instance
//         * Check if it is null, if true: Set the instance in the synchronized block and we check again if it is not null.
//         * if it is still null we will create our data base */
//        /* synchronized(LOCK) used to make sure everything that happens inside this block of code can't be accessed by other threads at the same time
//         * to make sure that there is not another thread that sets this instance to something that we already sit it (it is like try to insert two data in the same time)  */
//        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
//            instance?: createDataBase(context).also { instance = it }
//        }
//
//        /* Private function to create the data base
//         * This function will return RoomDatabase() object which ill be passed to instant of ArticleDataBase */
//        private fun createDataBase(context: Context) =
//            Room.databaseBuilder(
//                context.applicationContext,
//                ArticleDataBase::class.java,
//                "article_db.db"
//            ).build()
//    }
}

