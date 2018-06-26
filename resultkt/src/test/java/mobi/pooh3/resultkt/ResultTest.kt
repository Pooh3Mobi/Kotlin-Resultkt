package mobi.pooh3.resultkt

import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*

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
    fun get() {
    }

    @Test
    fun onSuccess() {
    }

    @Test
    fun onFailure() {
    }

    @Test
    fun map() {
    }

    @Test
    fun flatMap() {
    }

    @Test
    fun filter() {
    }
}