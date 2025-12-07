package com.tarlanus.simplevideotrimmer.utils

sealed class Resource<T>(val data: T? = null, val error: String? = null) {

    class LOADING<T> : Resource<T>()
    class SUCCESS<T>(data: T) : Resource<T>(data = data, error = null)
    class ERROR<T>(msg: String) : Resource<T>(data = null, error = msg)

}