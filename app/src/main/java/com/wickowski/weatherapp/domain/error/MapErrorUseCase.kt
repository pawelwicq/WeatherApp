package com.wickowski.weatherapp.domain.error

import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.presentation.ApiError
import com.wickowski.weatherapp.presentation.ErrorType
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MapErrorUseCase {

    fun execute(throwable: Throwable): ApiError = when (throwable) {
        is UnknownHostException, is SocketTimeoutException, is ConnectException -> ApiError(
            ErrorType.NETWORK_ERROR,
            R.string.network_error_message
        )
        is HttpException -> ApiError(ErrorType.HTTP_ERROR, R.string.http_error_message)
        else -> ApiError(ErrorType.UNKNOWN, R.string.unknown_error_message)
    }

}