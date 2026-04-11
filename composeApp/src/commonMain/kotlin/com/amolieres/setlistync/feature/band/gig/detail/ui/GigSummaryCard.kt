package com.amolieres.setlistync.feature.band.gig.detail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.core.domain.band.model.Gig
import org.jetbrains.compose.resources.stringResource
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.gig_field_date_none
import setlistsync.composeapp.generated.resources.gig_summary_no_venue
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun GigSummaryCard(gig: Gig, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimens.SpacingL),
            verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingS)
        ) {
            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingS)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.IconSizeMedium),
                    tint = MaterialTheme.colorScheme.primary
                )
                val dateText = gig.date?.toString()?.take(10)
                    ?: stringResource(Res.string.gig_field_date_none)
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (gig.date == null)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
            // Venue
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingS)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.IconSizeMedium),
                    tint = MaterialTheme.colorScheme.primary
                )
                val venueText = gig.venue ?: stringResource(Res.string.gig_summary_no_venue)
                Text(
                    text = venueText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (gig.venue == null)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
