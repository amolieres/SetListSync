package com.amolieres.setlistync.feature.main.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.core.domain.band.model.BandPresenter
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import org.jetbrains.compose.resources.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview

/**
 * Card representing a [BandPresenter] in the main list.
 * Displays the band name, member count, song count and setlist count.
 */
@Composable
fun BandItem(
    bandPresenter: BandPresenter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(AppDimens.SpacingL)) {

            // ── Name ──────────────────────────────────────────────────────────
            Text(bandPresenter.bandName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(AppDimens.SpacingM))

            // ── Stats: members + songs + setlists ─────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingL)) {
                BandStat(
                    icon = Icons.Default.Group,
                    label = pluralStringResource(Res.plurals.band_item_members, bandPresenter.memberCount, bandPresenter.memberCount)
                )
                BandStat(
                    icon = Icons.Default.MusicNote,
                    label = pluralStringResource(Res.plurals.band_detail_songs, bandPresenter.songCount, bandPresenter.songCount)
                )
                BandStat(
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    label = pluralStringResource(Res.plurals.band_item_setlists, bandPresenter.setListCount, bandPresenter.setListCount)
                )
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

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewBandFull = BandPresenter(
    bandId = "1",
    bandName = "The Rocketeers",
    memberCount = 3,
    songCount = 12,
    setListCount = 2
)

private val previewBandMinimal = BandPresenter(
    bandId = "2",
    bandName = "Silent Echo",
    memberCount = 1,
    songCount = 0
)

@Preview
@Composable
private fun BandItemFullPreview() {
    BandItem(bandPresenter = previewBandFull, onClick = {})
}

@Preview
@Composable
private fun BandItemMinimalPreview() {
    BandItem(bandPresenter = previewBandMinimal, onClick = {})
}
