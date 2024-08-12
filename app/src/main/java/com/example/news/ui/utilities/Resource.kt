package com.example.news.ui.utilities

import com.bumptech.glide.load.ResourceEncoder

/* This class recommended to use by Google to be used to wrap around our network responses [state]
 * That would be a generic class and it is very useful to differentiate between successful and error reponses
 * it also helps us to handle the loading state  */

/* sealed class is kinda of abstract class but we can define which classes are allowed to inherit from that resource class
 * In this class we will define only three different classes and only those classes are allowed to inherits from Resource class */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /* Success class has data because we have a success response */
    class Success<T>(data: T): Resource<T>(data)
    /* will take a error message and it may contain data so we will set it to null initially  */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    /* when our request was fired off then we will omit that loading state
     * when the response comes then we will instead omit that success or error state */
    class Loading<T>: Resource<T>()
}