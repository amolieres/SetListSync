package com.amolieres.setlistync.core.util

fun Int.formatDuration(): String {
    val m = this / 60
    val s = this % 60
    return "$m:${s.toString().padStart(2, '0')}"
}

fun Int.formatSetlistDuration(): String {
    val totalMinutes = this / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes.toString().padStart(2, '0')}min"
    else "${minutes}min"
}
