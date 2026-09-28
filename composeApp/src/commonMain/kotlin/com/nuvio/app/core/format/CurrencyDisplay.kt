package com.nuvio.app.core.format

/**
 * Formats a positive TMDB whole-dollar amount for display.
 *
 * TMDB uses zero for unknown budget and revenue values. Comma grouping is implemented in common
 * Kotlin so the output remains identical on macOS and Windows.
 */
fun formatUsdAmountForDisplay(amount: Long?): String? {
    if (amount == null || amount <= 0L) return null

    val digits = amount.toString()
    return buildString(digits.length + (digits.length - 1) / 3 + 1) {
        append('$')
        digits.forEachIndexed { index, character ->
            if (index > 0 && (digits.length - index) % 3 == 0) append(',')
            append(character)
        }
    }
}
