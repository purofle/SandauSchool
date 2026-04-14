package com.github.purofle.sandauschool.utils

import kotlinx.coroutines.CancellationException

inline fun <T> retry(
    times: Int = 3,
    onError: (attempt: Int, exception: Throwable) -> Unit,
    block: () -> T
): T {

    repeat(times) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            onError(attempt + 1, e)
            if (attempt == times - 1) {
                throw e
            }
        }
    }

    throw IllegalStateException("The retry block should have either returned a value or thrown an exception.")
}