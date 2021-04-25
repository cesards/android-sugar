package com.cesards.sugar.kotlin.core

import java.io.Serializable

//val ass = Result<String>
//val ass = Outcome<String, Error>()
//val ass = Failure

// Factories

inline fun <R : Any> runCatching(block: () -> R): Outcome<R, Throwable> {
    return try {
        success(block())
    } catch (e: Throwable) {
        failure(e)
    }
}

fun <V : Any> success(value: V): Success<V> = Success(value)
fun <E : Throwable> failure(error: E): Failure<E> = Failure(error)

// Many

fun <V : Any, E : Throwable> all(vararg results: Outcome<V, E>) = all(results.asIterable())


// Do we need this since the sealed class implements iterable??
fun <V : Any, E : Throwable> all (results: Iterable<Outcome<V, E>>): Outcome<List<V>, E> {
    return success(results.map {
        when (it) {
            is Success<V> -> it.value
            is Failure<E> ->
                // Short-circuit
                return it
        }
    })
}

fun <V: Any, E : Throwable> Outcome<V, E>.getOrDefault(default: V) : V = when (this) {
    is Success<V> -> value
    is Failure<E> -> default
}

fun <V : Any, E : Throwable> Outcome<V, E>.getOrElse(transformError: (E) -> V) : V = when (this) {
    is Success<V> -> value
    is Failure<E> -> transformError(errorValue)
}

fun <V : Any, V2 : Any, E : Throwable> Outcome<V, E>.flatMap(transformValue: (V) -> Outcome<V2, E>): Outcome<V2, E> = when (this) {
    is Success<V> -> transformValue(value)
    is Failure<E> -> this
}

// very similar to [mapError]
fun <V : Any, E : Throwable, E2 : Throwable> Outcome<V, E>.flatMapError(transformError: (E) -> Outcome<V, E2>): Outcome<V, E2> {
    return when (this) {
        is Success<V> -> this
        is Failure<E> -> transformError(errorValue)
    }
}

typealias SimpleOutcome<D> = Outcome<D, Throwable>

/**
 * A simple Result monad for Kotlin.
 * Alternatives:
 * - https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/
 */
sealed class Outcome<out Data: Any, out Err: Throwable> : Serializable, Iterable<Data> {

    /**
     * @return [true] if the outcome of the operation is successful, [false] otherwise.
     */
    abstract val valid: Boolean


    abstract val error: Err?

    fun getOrThrow(): Data = when (this) {
        is Success -> value
        is Failure -> throw NoSuchOutcomeException(errorValue)
    }

    fun getOrNull(): Data? = when (this) {
        is Success -> value
        is Failure -> null
    }

    fun <Result: Any> map(transformValue: (Data) -> Result): Outcome<Result, Err> = when (this) {
        is Success -> Success(transformValue(value))
        is Failure -> this
    }

    fun <MappedError : Throwable> mapError(transformError: (Err) -> MappedError): Outcome<Data, MappedError> = when (this) {
        is Success -> this
        is Failure -> Failure(transformError(errorValue))
    }

    fun <Result> fold(transformValue: (Data) -> Result, transformError: (Err) -> Result): Result = when (this) {
        is Success -> transformValue(value)
        is Failure -> transformError(errorValue)
    }
}

class Success <out D: Any> internal constructor (val value: D): Outcome<D, Nothing>() {
    override fun toString() = "Outcome.Success($value)"
    override fun hashCode() = value.hashCode() // value?.hashCode() ?: 0 (if nullable with Any?)
    override fun equals(other: Any?): Boolean = this === other || (other is Success<*> && value == other.value)

    override val valid: Boolean = true

    override val error: Nothing? = null

    override fun iterator(): Iterator<D> {
        TODO("Not yet implemented")
    }
}

class Failure <out E : Throwable> internal constructor (val errorValue: E): Outcome<Nothing, E>() {
    override fun toString() = "Result.Err($errorValue)"
    override fun hashCode() = errorValue.hashCode() // error?.hashCode() ?: 0 (if nullable with Any?)
    override fun equals(other: Any?): Boolean = (this === other) || (other is Failure<*> && errorValue == other.errorValue)

    override val valid: Boolean = false

    override val error: E = errorValue

    override fun iterator(): Iterator<Nothing> {
        TODO("Not yet implemented")
    }
}

private class NoSuchOutcomeException(throwable: Throwable) : java.lang.RuntimeException("Unfortunately we operation was unsuccessful", throwable)
















/**
 * Other name candidates: ActionResult, OperationResult,
 *
 * [Result]<[T]><[E]> is a type that represents either success ([Success]) or failure ([Failure]). It is used as a
 * return value for functions that can error whenever errors are recoverable.
 *
 * It is a sealed class with the subclasses:
 *  * [Success]<[T]>, representing success and containing a value
 *  * [Failure]<[E]>, representing error and containing an error value.
 */
sealed class Output<T : Any, E : Any> {/*: Iterable<T> {*/
    // Imperative style

    /**
     * `true` if the result is [Success]
     */
    abstract val valid: Boolean

    /**
     * `true` if the result is an [Failure]
     */
    abstract val error: Boolean

    /**
     * Extract the nullable value from this result, which will be null if the result is [Failure]
     */
    abstract val validData: T?

    /**
     * Extract a nullable error from this result, which will be null if the result is [Success]
     */
    abstract val errorData: E?

    // Functional style

    /**
     * Reduce either the [Success] value or the [Failure] value to a new value of type [R]
     * @param okFn The function to apply if the result is [Success] value
     * @param errFn The function to apply if the result is [Failure] value
     */
    abstract fun <R> reduce(okFn: (T) -> R, errFn: (E) -> R): R

    @Deprecated(
        "Use reduce, this function was renamed to match Kotlin's standard library naming convention",
        replaceWith = ReplaceWith("reduce(okFn, errFn)")
    )
    fun <R> fold(okFn: (T) -> R, errFn: (E) -> R): R = reduce(okFn, errFn)

    // Functor

    /**
     * Maps a [Result]<[T], [E]> to [Result]<[R], [E]> by applying a function to a contained [Success] value,
     * leaving an error value untouched
     *
     * @param body The function to apply to an ok result
     */
    abstract fun <R : Any> map(body: (T) -> R): Output<R, E>

    /**
     * Maps a [Result]<[T], [E]> to [Result]<[T], [R]> by applying a function to a contained [Failure] value,
     * leaving an ok value untouched
     *
     * @param body The function to apply to an ok result
     */
    abstract fun <R : Any> mapError(body: (E) -> R): Output<T, R>

    // Monad

    /**
     * Calls [body] if the result is [Success], otherwise returns the [Failure] value of `this`.
     * This function can be used for control flow based on [Result] values.
     */
    abstract fun <R : Any> flatMap(body: (T) -> Output<R, E>): Output<R, E>

    /**
     * Calls [body] if the result is [Failure], otherwise returns the [Success] value of `this`.
     * This function can be used for control flow based on [Result] values.
     */
    abstract fun <R : Any> flatMapError(body: (E) -> Output<T, R>): Output<T, R>

    // Side effecting

    /**
     * Perform the side effecting function [body] if the result is [Success], leaving the contained value untouched
     */
    abstract fun doOnOk(body: (T) -> Unit): Output<T, E>

    /**
     * Perform the side effecting function [body] if the result is [Failure], leaving the contained value untouched
     */
    abstract fun doOnErr(body: (E) -> Unit): Output<T, E>

    /**
     * Returns [other] if the result is [Success], otherwise returns the [Failure] value of `this`.
     */
    abstract infix fun <R : Any> and(other: Output<R, E>): Output<R, E>

    /**
     * Returns [other] if the result is [Failure], otherwise returns the [Success] value of `this`.
     */
    abstract infix fun <R : Any> or(other: Output<T, R>): Output<T, R>

    /**
     * Returns the contained [Success] value, or a [default] if the result is [Failure]
     */
    abstract fun orDefault(default: T): T

    /**
     * Returns a [Sequence] of the possibly contained [Success] value.
     * The [Sequence] yields one value if the result is [Success], otherwise an empty [Sequence]
     */
    //abstract fun toSequence(): Sequence<T>

    /**
     * Returns an [Iterator] of the possibly contained [Success] value.
     * The [Iterator] yields one value if the result is [Success], otherwise an empty [Iterator]
     */
    //final override fun iterator(): Iterator<T> = toSequence().iterator()
}

// Implementation

// Impl note: These implementations are not data classes as they do not need destructuring or the copy function
/*class Success<T : Any, E : Any>(val v: T) : Output<T, E>() {
    override val valid: Boolean get() = true
    override val error: Boolean get() = false
    override val validData: T? get() = v
    override val errorData: E? get() = null
    override fun <R> reduce(okFn: (T) -> R, errFn: (E) -> R): R = okFn(v)
    override fun <R : Any> map(body: (T) -> R): Output<R, E> = Success(body(v))
    override fun <R : Any> mapError(body: (E) -> R): Output<T, R> = Success(v)
    override fun <R : Any> flatMap(body: (T) -> Output<R, E>): Output<R, E> = body(v)
    override fun <R : Any> flatMapError(body: (E) -> Output<T, R>): Output<T, R> = Success(v)
    override fun doOnOk(body: (T) -> Unit): Output<T, E> = apply { body(v) }
    override fun doOnErr(body: (E) -> Unit): Output<T, E> = this
    override fun <R : Any> and(other: Output<R, E>): Output<R, E> = other
    override fun <R : Any> or(other: Output<T, R>): Output<T, R> = Success(v)
    override fun orDefault(default: T): T = v
    override fun toSequence(): Sequence<T> = sequenceOf(v)
    override fun toString(): String = "Ok($v)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Success<*, *>

        if (v != other.v) return false

        return true
    }

    override fun hashCode(): Int {
        return v.hashCode()
    }
}*/

/*class Failure<T : Any, E : Any>(val e: E) : Output<T, E>() {
    override val valid: Boolean get() = false
    override val error: Boolean get() = true
    override val validData: T? get() = null
    override val errorData: E? get() = e
    override fun <R> reduce(okFn: (T) -> R, errFn: (E) -> R): R = errFn(e)
    override fun <R : Any> map(body: (T) -> R): Output<R, E> = Failure(e)
    override fun <R : Any> mapError(body: (E) -> R): Output<T, R> = Failure(body(e))
    override fun <R : Any> flatMap(body: (T) -> Output<R, E>): Output<R, E> = Failure(e)
    override fun <R : Any> flatMapError(body: (E) -> Output<T, R>): Output<T, R> = body(e)
    override fun doOnOk(body: (T) -> Unit): Output<T, E> = this
    override fun doOnErr(body: (E) -> Unit): Output<T, E> = apply { body(e) }
    override fun <R : Any> and(other: Output<R, E>): Output<R, E> = Failure(e)
    override fun <R : Any> or(other: Output<T, R>): Output<T, R> = other
    override fun orDefault(default: T): T = default
    override fun toSequence(): Sequence<T> = emptySequence()
    override fun toString(): String = "Err($e)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Failure<*, *>

        if (e != other.e) return false

        return true
    }

    override fun hashCode(): Int {
        return e.hashCode()
    }
}*/

/*
@Suppress("UNUSED_PARAMETER")
fun <T : Any> resultOf(
    predicate: (Exception) -> Boolean = { it !is RuntimeException },
    body: () -> T
): Output<T, Exception> = try {
    Authenticator.Success(body())
} catch (e: Exception) {
    Failure(e)
}
*/

/**
 *
 * Returns either a list of the all results or the first error.
 *
 * @receiver Iterable<Result<T, E>>
 * @return Result<List<T>, E> List of values
 */
/*fun <T : Any, E : Any> Iterable<Output<T, E>>.valuesOrError(): Output<List<T>, E> {
    val okValues = mutableListOf<T>()
    forEach { result ->
        when (result) {
            is Authenticator.Success -> okValues.add(result.v)
            is Failure -> return Failure(result.e)
        }
    }

    return Authenticator.Success(okValues)
}*/
