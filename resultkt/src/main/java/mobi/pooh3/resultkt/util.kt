package mobi.pooh3.resultkt

import android.support.annotation.IntRange

fun <T> tryTimes(@IntRange(from = 1) max: Int = 1, body: () -> T): Result<T> {
    var lastError: Throwable? = null
    return (1..max).asSequence().map {
        try { Result.Success(body()) }
        catch (e: Throwable) { lastError = e ;Result.error<T>(e) }
    }.find { it is Result.Success } ?: Result.error(lastError!!)
}