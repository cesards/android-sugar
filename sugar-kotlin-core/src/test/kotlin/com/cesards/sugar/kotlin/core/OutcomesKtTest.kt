package com.cesards.sugar.kotlin.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

internal class OutcomesKtTest {

    @Test
    fun `success - should return Success`() {
        val expectedValue = 42
        val result = success(expectedValue)
        assertThat(result).isInstanceOf(Success::class)
        assertThat(result.value).isEqualTo(expectedValue)
        assertThat(result.valid).isTrue()
    }

    @Test
    fun `failure - should return Failure`() {
        val expectedValue = Error("This is an error.")
        val result = failure(expectedValue)
        assertThat(result).isInstanceOf(Failure::class)
        assertThat(result.errorValue).isEqualTo(expectedValue)
        assertThat(result.valid).isFalse()
    }

    @Test
    fun `getOrDefault - should return the original value when the outcome is success`() {
        // Arrange
        val expectedValue = 33

        // Act
        val firstResult = com.cesards.sugar.kotlin.core.runCatching { expectedValue }.getOrDefault(0)

        // Assert
        assertThat(firstResult).isEqualTo(expectedValue)
    }

    @Test
    fun `getOrDefault - should return the given value when the outcome is failure`() {
        // Arrange
        val expectedValue = 22

        // Act
        val result = com.cesards.sugar.kotlin.core.runCatching { throw IllegalStateException("\uD83D\uDCA5") }.getOrDefault(expectedValue)

        // Assert
        assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun `getOrElse - should return the original value when the outcome is success`() {
        // Arrange
        val expectedValue = 33

        // Act
        val firstResult = com.cesards.sugar.kotlin.core.runCatching { expectedValue }.getOrElse { 0 }

        // Assert
        assertThat(firstResult).isEqualTo(expectedValue)
    }

    @Test
    fun `getOrElse - should return the given value when the outcome is failure`() {
        // Arrange
        val expectedValue = 22

        // Act
        val result = com.cesards.sugar.kotlin.core.runCatching { throw IllegalStateException("\uD83D\uDCA5") }.getOrElse { expectedValue }

        // Assert
        assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun `fold - should return left value when the outcome is success`() {
        // Arrange
        val expectedValue = 22

        // Act
        val result = success(121212).fold({ expectedValue }, { 909 })

        // Assert
        assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun `fold - should return left value when the outcome is failure`() {
        // Arrange
        val expectedValue = 22

        // Act
        val result = failure(Error("Random error")).fold({ 909 }, { expectedValue })

        // Assert
        assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun `map - should apply operations when outcome is success`() {
        // Arrange
        val initialValue = 10
        val expectedValue = 10 + 1 + 1

        // Act
        val result = success(initialValue).map { it + 1 }.map { it + 1 }

        // Assert
        assertThat(result.getOrNull()).isEqualTo(expectedValue)
    }

    @Test
    fun `map - should not apply operations when outcome is failure`() {
        // Arrange
        val expectedValue = IllegalStateException("\uD83D\uDCA5")

        // Act
        val result = runCatching<Int> { throw expectedValue }.map { it + 1 }.map { it + 2 }

        // Assert
        assertThat(result.error).isEqualTo(expectedValue)
    }


    @Test
    fun `mapError - should apply operations when outcome is success`() {
        // Arrange
        val initialValue = 10
        val expectedValue = 10 + 1 + 1

        // Act
        val result = success(initialValue).map { it + 1 }.map { it + 1 }

        // Assert
        assertThat(result.getOrNull()).isEqualTo(expectedValue)
    }

    @Test
    fun `map - should not apply operations when outcome is failure`() {
        // Arrange
        val expectedValue = IllegalStateException("\uD83D\uDCA5")

        // Act
        val result = runCatching<Int> { throw expectedValue }.map { it + 1 }.map { it + 2 }

        // Assert
        assertThat(result.error).isEqualTo(expectedValue)
    }



    @Test
    fun `flatMap - should apply operations when outcome is success`() {
        // Arrange
        val expectedValue = 4 + 1 + 1

        // Act
        val result: SimpleOutcome<Int> = success(4).flatMap { success(it + 1) }.flatMap { success(it + 1) }

        // Assert
        assertThat(result.getOrNull()).isEqualTo(expectedValue)
    }

    @Test
    fun `flatMap - should not apply operations when outcome is failure`() {
        // Arrange
        val expectedValue = IllegalStateException("\uD83D\uDCA5")

        // Act
        val result = runCatching<Int> { throw expectedValue }.flatMap { failure(IllegalArgumentException("illegal")) }

        // Assert
        assertThat(result.error).isEqualTo(expectedValue)
    }

    @Test
    fun `flatMapError - should not apply operations when outcome is success`() {
        // Arrange
        val expectedValue = 4

        // Act
        val result: SimpleOutcome<Int> = runCatching<Int> { 4 }.flatMapError { success(3) }

        // Assert
        assertThat(result.getOrNull()).isEqualTo(expectedValue)
    }

    @Test
    fun `flatMapError - should apply operations when outcome is failure`() {
        // Arrange
        val expectedValue = IllegalStateException("\uD83D\uDCA5")

        // Act
        val result : SimpleOutcome<Int> = runCatching<Int> { throw IllegalArgumentException("illegal") }
            .flatMapError { failure(expectedValue) }

        // Assert
        assertThat(result.error).isEqualTo(expectedValue)
    }









    /*@Test
    fun testMapError() {
        assertEquals(
            "mapError cannot transition ok result",
            Result.Ok<Int>(1),
            Result.ok(1).mapError { "failure" }
        )
        assertEquals(
            "mapError transforms err",
            Result.Err<String>("failure-mapped"),
            Result.err("failure").mapError { it + "-mapped" }
        )
    }*/

    /*@Test
    fun testAll() {
        assertEquals(
            "Result.all combines ok values",
            Result.Ok<List<Int>>(listOf(1, 2, 3)),
            Result.all(Result.ok(1), Result.ok(2), Result.ok(3))
        )

        assertEquals(
            "Result.all becomes first err",
            Result.Err<String>("a"),
            Result.all(
                Result.ok(1), Result.err("a"),
                Result.ok(2), Result.err("b"),
                Result.ok(3), Result.err("c")
            )
        )
    }*/

    /*@Test
    fun getOrThrowOk() {
        assertEquals("Result.getOrThrow() returns Result.Ok value", 42, Result.ok(42).getOrThrow())
    }*/

    /*@Test(expected = UnwrapException::class)
    fun getOrThrowErr() {
        Result.err(42).getOrThrow()
    }*/

    /*@Test
    fun testEquals() {
        assertEquals(Result.ok(42), Result.ok(42))
        assertEquals(Result.err("failure"), Result.err("failure"))
        assertEquals(Result.ok(null), Result.ok(null))
        assertEquals(Result.err(null), Result.err(null))
        assertNotEquals(Result.ok(null), Result.err(null))
        assertNotEquals(Result.ok(42), Result.err("failure"))
        assertNotEquals(Result.ok(42), Result.ok(-42))
        assertNotEquals(Result.err("failure A"), Result.err("failure B"))
    }*/

    /*@Test
    fun testAssignResultOk() {
        val ok: Result.Ok<String> = Result.ok("ok")
        val result: Result<String, Float> = ok
        val result2: Result<String, Int> = ok
    }*/

    /*@Test
    fun testAssignResultErr() {
        val err: Result.Err<String> = Result.err("err")
        val result: Result<Float, String> = err
        val result2: Result<Int, String> = err
    }*/




}
