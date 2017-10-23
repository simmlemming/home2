package org.home2

/**
 * Created by mtkachenko on 23/10/17.
 */
class NetworkResource<out T> private constructor(val state: NetworkResource.State, val data: T?, val exception: Throwable?) {
    enum class State {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): NetworkResource<T> {
            return NetworkResource<T>(State.SUCCESS, data, null)
        }

        fun <T> error(throwable: Throwable): NetworkResource<T> {
            return NetworkResource<T>(State.ERROR, null, throwable)
        }

        fun <T> loading(data: T?): NetworkResource<T> {
            return NetworkResource<T>(State.LOADING, data, null)
        }
    }
}
