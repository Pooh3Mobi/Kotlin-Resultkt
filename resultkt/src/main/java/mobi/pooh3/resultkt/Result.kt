package mobi.pooh3.resultkt


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