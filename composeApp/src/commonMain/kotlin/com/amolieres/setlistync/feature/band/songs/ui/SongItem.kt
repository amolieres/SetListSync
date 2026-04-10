package com.amolieres.setlistync.feature.band.songs.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.model.SongKey
import com.amolieres.setlistync.core.util.formatDuration
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.song_cd_delete

@Composable
fun SongItem(
    song: Song,
    noteNotation: NoteNotation,
    onEdit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    position: Int? = null,
    dragHandleModifier: Modifier? = null,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onEdit,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimens.SpacingL, vertical = AppDimens.SpacingM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingM)
        ) {
            if (position != null) {
                Box(
                    modifier = Modifier.size(AppDimens.IconSizeMedium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = position.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.IconSizeMedium),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val subtitleParts = buildList {
                    song.originalArtist?.let { add(it) }
                    add(song.durationSeconds.formatDuration())
                    song.key?.let { add(it.display(noteNotation)) }
                    song.tempo?.let { add("$it BPM") }
                }
                Text(
                    text = subtitleParts.joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.song_cd_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            if (dragHandleModifier != null) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = null,
                    modifier = dragHandleModifier.size(AppDimens.IconSizeMedium),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun SongItemFullPreview() {
    SongItem(
        song = Song(
            id = SongId("1"),
            title = "Highway to Hell",
            durationSeconds = 208,
            originalArtist = "AC/DC",
            key = SongKey.A_MINOR,
            tempo = 116
        ),
        noteNotation = NoteNotation.EN,
        onEdit = {},
        onDelete = {}
    )
}

@Preview
@Composable
private fun SongItemFrenchPreview() {
    SongItem(
        song = Song(
            id = SongId("2"),
            title = "Summer Rain",
            durationSeconds = 213,
            originalArtist = "The Rocketeers",
            key = SongKey.A_MINOR,
            tempo = 120
        ),
        noteNotation = NoteNotation.FR,
        onEdit = {},
        onDelete = {}
    )
}

@Preview
@Composable
private fun SongItemMinimalPreview() {
    SongItem(
        song = Song(
            id = SongId("3"),
            title = "Electric Nights",
            durationSeconds = 187
        ),
        noteNotation = NoteNotation.EN,
        onEdit = {},
        onDelete = {}
    )
}
