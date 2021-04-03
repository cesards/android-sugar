package com.cesards.sugar.kotlin.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun Boolean.alsoIfTrue(block: () -> Unit) : Boolean {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (this) block()
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun Boolean.alsoIfFalse(block: () -> Unit) : Boolean {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (!this) block()
    return this
}
