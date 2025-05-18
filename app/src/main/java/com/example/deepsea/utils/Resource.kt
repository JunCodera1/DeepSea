package com.example.deepsea.utils

sealed class Resource<T>(
    val data: T? = null,
    val error: String? = null,
    val isLoading: Boolean = false
) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data = data, error = message)
    class Loading<T>(data: T? = null) : Resource<T>(data = data, isLoading = true)
}