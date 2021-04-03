package com.cesards.sugar.kotlin.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class GenericsKtTest {

    @Test
    fun `applyIf - given a false predicate, should not apply the given block`() {
        val burritoWrap = BurritoWrap(amount = 3)
        burritoWrap.applyIf(false) { amount = 5 }
        assertThat(burritoWrap.amount).isEqualTo(3)
    }

    @Test
    fun `applyIf - given a true predicate, should apply the given block`() {
        val burritoWrap = BurritoWrap(amount = 3)
        burritoWrap.applyIf(true) { amount = 5 }
        assertThat(burritoWrap.amount).isEqualTo(5)
    }

    @Test
    fun `applyIf - given a false predicate result, should not apply the given block`() {
        val burritoWrap = BurritoWrap(amount = 3)
        burritoWrap.applyIf({ amount != 3 }, { amount = 5 })
        assertThat(burritoWrap.amount).isEqualTo(3)
    }

    @Test
    fun `applyIf - given a true predicate result, should not apply the given block`() {
        val burritoWrap = BurritoWrap(amount = 3)
        burritoWrap.applyIf({ amount != 5 }, { amount = 5 })
        assertThat(burritoWrap.amount).isEqualTo(5)
    }

    private class BurritoWrap(var amount : Int)
}
