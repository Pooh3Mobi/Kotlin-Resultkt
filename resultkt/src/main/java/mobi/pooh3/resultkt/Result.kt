package mobi.pooh3.resultkt

/*
Copyright (c) <2018> <PooheMobi@GitHub>
Released under the MIT license
https://opensource.org/licenses/mit-license.php
*/

sealed class Result<T> {

    abstract fun isSuccess() : Boolean
    abstract fun isFailure() : Boolean
    abstract fun get() : T

    data class Success<T>(val value: T) : Result<T>() {
        override fun isSuccess() = true
        override fun isFailure() = false
        override fun get(): T = value
    }

    data class Failure<T>(val e: Throwable) : Result<T>() {
        override fun isSuccess() = false
        override fun isFailure() = true
        override fun get(): T = throw NoSuchElementException()
    }

    object Ignore : Result<Any>() {
        override fun isSuccess(): Boolean = false
        override fun isFailure(): Boolean = false
        override fun get(): Any = throw NoSuchElementException()
    }

    companion object {
        fun <T> ignore(): Result<T> = Ignore as Result<T>
        fun <T> just(value: T) : Result<T> = Success(value)
        fun <T> error(e: Throwable) : Result<T> = Failure(requireNotNull(e))
    }

    fun onSuccess(body: (T) -> Unit) : Result<T> {
        if (this is Success) body(value)
        return this
    }

    fun onFailure(body: (Throwable) -> Unit) : Result<T> {
        if (this is Failure) body(e)
        return this
    }

    fun <U> map(f: (T) -> U): Result<U> = when(this) {
        is Success -> Success(f(value))
        is Failure -> error(e)
        else -> ignore()
    }

    fun <U> flatMap(f: (T) -> Result<U>): Result<U> = when(this) {
        is Success -> f(value)
        is Failure -> error(e)
        else -> ignore()
    }

    fun filter(f: (T) -> Boolean): Result<T> =
            if (this is Success && f(value)) this
            else ignore()

    fun recoverWith(f: (Throwable) -> Result<T>): Result<T> = when(this) {
        is Success -> Success(value)
        is Failure -> f.invoke(e)
        else -> ignore()
    }

    fun recover(f: () -> Result<T>): Result<T> = when(this) {
        is Success -> Success(value)
        is Failure -> f.invoke()
        else -> ignore()
    }
}