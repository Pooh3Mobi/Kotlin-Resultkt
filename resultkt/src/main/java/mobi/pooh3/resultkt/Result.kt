package mobi.pooh3.resultkt

/*
Copyright (c) <2018> <PooheMobi@GitHub>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
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
        fun <T> error(e: Throwable) : Result<T> = Failure(e)
    }

    fun onSuccess(body: (T) -> Unit) : Result<T> {
        if (this is Success) body(value)
        return this
    }

    fun onFailure(body: (Throwable) -> Unit) : Result<T> {
        if (this is Failure) body(e)
        return this
    }

    fun <U> map(f: (T) -> U): Result<U> =
            if (this is Success) Success(f(value)) else ignore()


    fun <U> flatMap(f: (T) -> Result<U>): Result<U> =
            if (this is Success) f(value) else ignore()

    fun filter(f: (T) -> Boolean): Result<T> =
            if (this is Success) {
                if(f(value)) this else ignore()
            } else this

}