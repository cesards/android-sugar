package com.cesards.sugar.kotlin.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class BooleansKtTest {

    @Test
    fun `alsoIfTrue - given a false predicate, should not invoke the given block`() {
        // Arrange
        val track = Track()

        // Act
        track(result = false).alsoIfTrue { track.invocations++ }

        // Assert
        assertThat(track.invocations).isEqualTo(1)
    }

    @Test
    fun `alsoIfTrue - given a true predicate, should invoke the given block`() {
        // Arrange
        val track = Track()

        // Act
        track(result = true).alsoIfTrue { track.invocations++ }

        // Assert
        assertThat(track.invocations).isEqualTo(2)
    }

    @Test
    fun `alsoIfFalse - given a true predicate, should not invoke the given block`() {
        // Arrange
        val track = Track()

        // Act
        track(result = true).alsoIfFalse { track.invocations++ }

        // Assert
        assertThat(track.invocations).isEqualTo(1)
    }

    @Test
    fun `alsoIfFalse - given a false predicate, should invoke the given block`() {
        // Arrange
        val track = Track()

        // Act
        track(result = false).alsoIfFalse { track.invocations++ }

        // Assert
        assertThat(track.invocations).isEqualTo(2)
    }

    private class Track {
        var invocations = 0

        operator fun invoke(result: Boolean) : Boolean{
            invocations++
            return result
        }
    }

}
