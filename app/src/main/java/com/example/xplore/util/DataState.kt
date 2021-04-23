package com.example.xplore.util

sealed class DataState<out R> {
    data class Success<out T>(val response: T): DataState<T>()
    data class Error(val error:String) : DataState<Nothing>()
    data class Loading(val message: String) : DataState<Nothing>()
}