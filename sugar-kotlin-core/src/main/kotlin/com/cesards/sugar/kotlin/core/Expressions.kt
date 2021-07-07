package com.cesards.sugar.kotlin.core

/**
 * This components helps us to verify in an exhaustive way, every possible case of our sealed classes when being
 * evaluated using "when" and its result is not an expression (it doesn't have a return value). With this hack, our when
 * returns a value so it becomes an expression and the compiler needs to verify all the possible results.
 *
 * The decision behind using an inline function behind an object instead of a property is to avoid making an extension
 * function available for any object.
 *
 * There is a ticket on YouTrack discussing official language level support for an "exhaustive" or "sealed" when
 * statement, so that this kind of workaround isn’t needed:
 * https://discuss.kotlinlang.org/t/algebraic-data-types-are-not-exhaustive/1857/5
 */
object Do {
    infix fun <T : Any> exhaustive(any: T): T = any
}
