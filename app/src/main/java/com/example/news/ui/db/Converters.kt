package com.example.news.ui.db

import androidx.room.TypeConverter
import com.example.news.ui.RetrofitNewsData.Source

/* This class will be used to convert any custom class data type in the entity (Article table)
 *  Such as attribute source which it is an object of custom class called Source
 *  Room can only handle primitive data types but not custom own classes, so we will use Converter class to solve this problem */
class Converters {
    /* source object contain both ID and name variables, we will only get the name and put it in out entity(Article table) as a string instead of source object */
    /* This means whenever we get a source object, Room should convert that source object into string by taking name of that source*/
    @TypeConverter /* to tell room this is a converter function */
    fun fromSource(source: Source?):String?{
        return source?.name
    }

    /* This function is the revers the previous function */
    @TypeConverter
    fun toSource(name: String?):Source{
        return Source(name,name)
    }

    /* Now we need to tell our data base we want to add this type converters to that data base */
}