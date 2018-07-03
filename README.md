# Kotlin-Resultkt

Resultkt Sample Code

```kotlin
fun main(args: Array<String>) {
    val rep = Repository()
    val userId = UserId("test")
    tryTimes(1) { rep.findCachedUserData(userId) }
            .recover { tryTimes(3){ rep.findUserData(userId) } }
            .onSuccess{ handleSuccess() }
            .onFailure{ handleFailure() }
    // print failure
}

fun handleSuccess() = print("Success")
fun handleFailure() = print("Failure")

data class UserData(val userId: UserId, val name: String)
data class UserId(val userId: String)

class Repository {
    fun findCachedUserData(usrId: UserId): UserData = throw UnsupportedOperationException()
    fun findUserData(usrId: UserId): UserData = throw UnsupportedOperationException()
}


fun <T> tryTimes(@IntRange(from = 1) max: Int, body: () -> T): Result<T> {
    var lastError: Throwable? = null
    return (1..max).asSequence().map {
        try { Result.Success(body()) }
        catch (e: Throwable) { lastError = e ;Result.error<T>(e) }
    }.find { it is Result.Success } ?: Result.error(lastError!!)
}
```
