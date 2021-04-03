package com.cesards.sugar.kotlin.core

fun <T> lazyOnMainThread(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)
