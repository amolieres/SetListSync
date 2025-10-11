package com.amolieres.setlistync

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform