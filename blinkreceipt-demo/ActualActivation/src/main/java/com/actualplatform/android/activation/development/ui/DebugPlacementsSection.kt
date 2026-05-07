package com.actualplatform.android.activation.development.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.models.PlacementLayout
import com.actualplatform.android.activation.development.R

@Composable
internal fun DebugPlacementsSection(
    forcePlacements: Set<String>,
    onForcePlacementsChange: (Set<String>) -> Unit,
) {
    Text(stringResource(R.string.activation_section_debug_placements), style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    PlacementLayout.entries.forEach { layout ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(layout.name, modifier = Modifier.weight(1f))
            Switch(
                checked = layout.name in forcePlacements,
                onCheckedChange = { checked ->
                    onForcePlacementsChange(
                        if (checked) forcePlacements + layout.name
                        else forcePlacements - layout.name
                    )
                },
            )
        }
    }
}
