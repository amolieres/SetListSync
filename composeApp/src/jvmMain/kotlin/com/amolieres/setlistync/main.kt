package com.amolieres.setlistync

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.ic_launcher

fun main() = application {
    val icon = painterResource(Res.drawable.ic_launcher)
    Window(
        onCloseRequest = ::exitApplication,
        title = "SetListSync",
        icon = icon,
    ) {
        App()
    }
}