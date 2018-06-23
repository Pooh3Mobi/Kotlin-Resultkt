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

class Result<T, E> private constructor(private val orNull: T?, private val e: E?) {

    val error: E get() = e
            ?: throw NoSuchElementException("No error present")
    val value: T = orNull
            ?: throw NoSuchElementException("No value present")
    val hasValue: Boolean get() = orNull != null
    val hasError: Boolean get() = e != null

    fun filter(predicate: (T?) -> Boolean): Result<T, E> =
            if (hasError || predicate.invoke(orNull)) this else empty()

    fun <U> map(mapper: (T?) -> U?): Result<U?, E> =
            if (hasError) empty() else Result.ofNullable(mapper.invoke(orNull))

    fun <U> flatMap(mapper: (T?) -> Result<U, E>): Result<U, E> =
            if (hasError) empty() else requireNotNull(mapper.invoke(orNull))

    fun getOrDefault(default: () -> T): T = orNull ?: default.invoke()
    fun getOrElse(default: (E) -> T): T = if (e != null) default.invoke(e) else value

    fun onSuccess(successConsumer: (T?) -> Unit): Result<T, E> {
        if (e == null) successConsumer.invoke(orNull)
        return this
    }

    fun onError(errorConsumer: (E?) -> Unit): Result<T, E> {
        if (e != null) errorConsumer.invoke(e)
        return this
    }

    @Throws(exceptionClasses = [(Throwable::class)])
    fun <X : Throwable> orElseThrow(exceptionSupplier: () -> X): T =
            value ?: throw exceptionSupplier.invoke()

    override fun toString(): String = "Result[${orNull?:e}]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Result<*, *>
        if (orNull != other.orNull) return false
        if (e != other.e) return false
        return true
    }

    override fun hashCode(): Int {
        var result = orNull?.hashCode() ?: 0
        result = 31 * result + (e?.hashCode() ?: 0)
        return result
    }

    companion object {
        private val EMPTY = Result<Any, Any>(null, null)

        @Suppress("UNCHECKED_CAST")
        fun <T : Any?, E : Any?> empty(): Result<T, E> = EMPTY as Result<T, E>
        fun <T : Any, E : Any?> of(value: T): Result<T, E> = Result(requireNotNull(value), null)
        fun <T : Any?, E : Any?> ofNullable(value: T?): Result<T, E> = Result(value, null)
        fun <T : Any?, E : Any> errorOf(e: E): Result<T, E> = Result(null, requireNotNull(e))
    }
}