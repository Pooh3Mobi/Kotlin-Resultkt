package mobi.pooh3.sample

import mobi.pooh3.resultkt.Result

fun main(args: Array<String>) {
    val rep = Repository()
    val userId = UserId("test")
    times(1) { rep.findCachedUserData(userId) }
            .recover { times(3){ rep.findUserData(userId) } }
            .onSuccess{ handleSuccess() }
            .onFailure{ handleFailure() }

}

fun handleSuccess() = print("Success")
fun handleFailure() = print("Failure")

data class UserData(val userId: UserId, val name: String)
data class UserId(val userId: String)

class Repository {
    fun findCachedUserData(usrId: UserId): UserData = throw UnsupportedOperationException()
    fun findUserData(usrId: UserId): UserData = throw UnsupportedOperationException()
}


fun <T> times(max: Int, body: () -> T): Result<T> =
        (0..max).asSequence().map {
            try { Result.Success(body()) }
            catch (e: Throwable) { Result.error<T>(e) }
        }.find { it is Result.Success } ?: Result.error(NoSuchElementException())