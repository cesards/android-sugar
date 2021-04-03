package com.cesards.sugar.kotlin.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@OptIn(ExperimentalContracts::class)
inline fun <T> T.applyIf(predicate: Boolean, block: T.() -> Unit) : T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (predicate) block()
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> T.applyIf(predicate: T.() -> Boolean, block: T.() -> Unit) : T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (predicate(this)) block()
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> T.alsoIf(predicate: Boolean, block: (T) -> Unit) : T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (predicate) block(this)
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> T.alsoIf(predicate: T.() -> Boolean, block: (T) -> Unit) : T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (predicate()) block(this)
    return this
}
