package mobi.pooh3.sample

import mobi.pooh3.resultkt.tryTimes

fun main(args: Array<String>) {
    val rep = Repository()
    val userId = UserId("test")
    tryTimes{ rep.findCachedUserData(userId) }
            .recover { tryTimes(3){ rep.findUserData(userId) } }
            .onSuccess(handleSuccess())
            .onFailure(handleFailure())

}

fun handleSuccess() = { ud: UserData -> print("Success:$ud")  }
fun handleFailure() = { e: Throwable -> print("Failure:$e") }

data class UserData(val userId: UserId, val name: String)
data class UserId(val userId: String)

class Repository {
    fun findCachedUserData(usrId: UserId): UserData = throw UnsupportedOperationException()
    fun findUserData(usrId: UserId): UserData = throw UnsupportedOperationException()
}
