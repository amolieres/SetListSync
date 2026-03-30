@file:OptIn(ExperimentalTime::class)

package com.amolieres.setlistync.feature.main.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.model.Role
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Card representing a [Band] in the main list.
 * Displays the band name, musical genres, member count, setlist count,
 * and the next upcoming concert (date + venue) if one exists.
 */
@Composable
fun BandItem(
    band: Band,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = Clock.System.now()
    val nextGig = band.gigs
        .filter { it.date != null && it.date!! > now }
        .minByOrNull { it.date!! }
    val setlistCount = band.gigs.sumOf { it.setListIds?.size ?: 0 }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(AppDimens.SpacingL)) {

            // ── Name ──────────────────────────────────────────────────────────
            Text(band.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            // ── Genres ────────────────────────────────────────────────────────
            if (band.genres.isNotEmpty()) {
                Spacer(Modifier.height(AppDimens.SpacingXxs))
                Text(
                    band.genres.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(AppDimens.SpacingM))

            // ── Stats: members + setlists ─────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingL)) {
                BandStat(
                    icon = Icons.Default.Group,
                    label = pluralStringResource(Res.plurals.band_item_members, band.members.size, band.members.size)
                )
                BandStat(
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    label = pluralStringResource(Res.plurals.band_item_setlists, setlistCount, setlistCount)
                )
            }

            // ── Next concert ──────────────────────────────────────────────────
            if (nextGig != null) {
                Spacer(Modifier.height(AppDimens.SpacingS))
                HorizontalDivider()
                Spacer(Modifier.height(AppDimens.SpacingS))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingXxs)
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(AppDimens.IconSizeSmall),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        buildString {
                            append(nextGig.date!!.toDisplayDate())
                            nextGig.venue?.let { append("  ·  $it") }
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// ── Private helpers ───────────────────────────────────────────────────────────

@Composable
private fun BandStat(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingXxs)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(AppDimens.IconSizeSmall),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/**
 * Formats a [kotlin.time.Instant] as a human-readable date string (e.g. "25 déc. 2025").
 * Uses ISO-8601 string parsing — no extra dependency required.
 */
private fun Instant.toDisplayDate(): String {
    val iso = toString().substringBefore('T') // "YYYY-MM-DD"
    val parts = iso.split("-")
    if (parts.size != 3) return iso
    val (year, month, day) = parts
    val monthName = when (month.toIntOrNull()) {
        1 -> "jan."; 2 -> "fév."; 3 -> "mars"; 4 -> "avr."; 5 -> "mai"; 6 -> "juin"
        7 -> "juil."; 8 -> "août"; 9 -> "sept."; 10 -> "oct."; 11 -> "nov."; 12 -> "déc."
        else -> month
    }
    return "${day.trimStart('0')} $monthName $year"
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewBandFull = Band(
    id = "1",
    name = "The Rocketeers",
    members = listOf(
        BandMember(id = "1", userId = null, nickname = "John", roles = listOf(Role.VOCALS, Role.GUITAR)),
        BandMember(id = "2", userId = null, nickname = "Jane", roles = listOf(Role.DRUMS)),
        BandMember(id = "3", userId = null, nickname = "Bob", roles = listOf(Role.BASS))
    ),
    genres = listOf("Rock", "Indie"),
    gigs = listOf(
        Gig(
            id = "g1",
            bandId = "1",
            venue = "Le Bataclan, Paris",
            date = Instant.parse("2025-12-25T20:00:00Z"),
            setListIds = listOf("sl1", "sl2")
        )
    )
)

private val previewBandMinimal = Band(
    id = "2",
    name = "Silent Echo",
    members = listOf(BandMember(id = "4", userId = null))
)

@Preview
@Composable
private fun BandItemFullPreview() {
    BandItem(band = previewBandFull, onClick = {})
}

@Preview
@Composable
private fun BandItemMinimalPreview() {
    BandItem(band = previewBandMinimal, onClick = {})
}
