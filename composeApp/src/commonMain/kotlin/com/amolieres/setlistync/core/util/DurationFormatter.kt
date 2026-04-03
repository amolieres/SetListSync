package com.amolieres.setlistync.core.util

fun Int.formatDuration(): String {
    val m = this / 60
    val s = this % 60
    return "$m:${s.toString().padStart(2, '0')}"
}
