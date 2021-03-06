package mobi.pooh3.resultkt

import org.hamcrest.CoreMatchers.*
import org.junit.Test

import org.junit.Assert.*
import java.util.*

class ResultTest {

    @Test
    fun isSuccess_Success_true() {
        val success = Result.just(Object()).isSuccess()
        assertThat(success, `is`(true))
    }

    @Test
    fun isFailure_Success_false() {
        val failure = Result.just(Object()).isFailure()
        assertThat(failure, `is`(false))    }

    @Test
    fun isSuccess_Failure_false() {
        val success = Result.error<Any>(Throwable()).isSuccess()
        assertThat(success, `is`(false))
    }

    @Test
    fun isFailure_Failure_true() {
        val failure = Result.error<Any>(Throwable()).isFailure()
        assertThat(failure, `is`(true))
    }

    @Test
    fun get_NotNull_fromSuccess() {
        val obj = Object()
        val successObj = Result.just(obj).get()
        assertThat(successObj , `is`(obj))
    }

    @Test
    fun get_NullValue_fromSuccess() {
        val successObj = Result.just(null).get()
        assertThat(successObj , `is`(nullValue()))
    }

    @Test
    fun get_fromFailure() {
        try {
            Result.error<Any>(Throwable()).get()
            fail()
        } catch (e: NoSuchElementException) {

        }
    }

    @Test
    fun onSuccess_NotNull() {
        Result.just(Object())
                .onSuccess {
                    assertThat(it, `is`(notNullValue()))
                }
    }

    @Test
    fun onSuccess_NullValue() {
        Result.just(null)
                .onSuccess {
                    assertThat(it, `is`(nullValue()))
                }
    }

    @Test
    fun onFailure() {
        Result.error<Any>(Throwable())
                .onFailure {
                    assertThat(it, `is`(notNullValue()))
                }
    }

    @Test
    fun success_callingOnSuccess_notCallingOnFailure() {
        Result.just(Object())
                .onSuccess {
                    assertThat(it, `is`(notNullValue()))
                }.onFailure {
                    fail()
                }
    }

    @Test
    fun failure_callingOnSuccess_notCallingOnFailure() {
        Result.error<Any>(Throwable())
                .onSuccess {
                    fail()
                }.onFailure {
                    assertThat(it, `is`(notNullValue()))
                }
    }

    @Test
    fun map() {
        val mapped = Result.just("1010")
                .map {
                    it.toInt() + 5
                }.get()
        assertThat(mapped, `is`(1015))
    }

    @Test
    fun flatMap() {
        val res1000 = Result.just(10000)
        val flatMapped = Result.just(1.2233)
                .flatMap {
                    fn -> res1000.map { fn * it }
                }.get()
        assertThat(flatMapped, `is`(12233.0))
    }

    @Test
    fun filter() {
        (1..10).asSequence().map {
            Result.just(it)
        }.forEach {
            it.filter { n ->
                n < 5
            }.onSuccess { n ->
                assertTrue(n in 0..4)
            }
        }
    }

    @Test
    fun recoverWith() {
        val value = Result.error<String>(Throwable())
                .recoverWith {
                    Result.Success("test")
                }.get()
        assertThat(value,`is`("test"))
    }

    @Test
    fun recover() {
        val value = Result.error<String>(Throwable())
                .recover {
                    Result.Success("test")
                }.get()
        assertThat(value,`is`("test"))
    }

    @Test fun monadLaws() {
        val x = "1000"
        val f: (String) -> Result<Int> = { Result.just(it.toInt()) }
        val g: (Int) -> Result<Double> = { Result.just(it.toDouble()) }

        val resultStringX = Result.just(x)

        assertTrue(
                resultStringX.flatMap(f) ==  f(x)
        )
        assertTrue(
                resultStringX.flatMap { Result.just(it) } == resultStringX
        )
        assertTrue(
                resultStringX.flatMap { f(it).flatMap(g) } == resultStringX.flatMap(f).flatMap(g)
        )
    }

    @Test fun functorLaws() {

        fun <T> id(x: T): T = x
        val x = "1000"
        val f: (String) -> Int = { it.toInt() }
        val g: (Int) -> Double = { it.toDouble() }

        val resultStringX = Result.just(x)

        assertTrue(
                resultStringX.map{ id(it) } == id(resultStringX)
        )
        assertTrue(
                resultStringX.map { g(f(it)) } == resultStringX.map(f).map(g)
        )
    }
}