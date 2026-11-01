package com.actualplatform.android.activation.development.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.ConsentState
import com.actualplatform.activation.RdpOwner
import com.actualplatform.android.activation.development.R

/**
 * Edits the privacy values this app states to the activation client.
 *
 * [storedRejection] is shown while the SDK rejects the values currently stored: the client is
 * then running on the unresolved defaults, which the fields, showing the stored values, do not
 * reveal on their own.
 */
@Composable
internal fun PrivacySection(
    sellOrShareStatus: ConsentState,
    onDataSaleOrSharingChange: (ConsentState) -> Unit,
    personalizationStatus: ConsentState,
    onAdPersonalizationChange: (ConsentState) -> Unit,
    disclosureVersion: String,
    onDisclosureVersionChange: (String) -> Unit,
    disclosureVersionError: String? = null,
    storedRejection: String? = null,
    rdpOwner: RdpOwner,
    onRdpOwnerChange: (RdpOwner) -> Unit,
) {
    Text(stringResource(R.string.activations_section_privacy), style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        stringResource(R.string.activations_privacy_help),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    if (storedRejection != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            storedRejection,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
    Spacer(modifier = Modifier.height(8.dp))

    SegmentedButtonsRow(
        label = stringResource(R.string.activations_label_data_sale_or_sharing),
        entries = ConsentState.entries,
        selected = sellOrShareStatus,
        onSelected = onDataSaleOrSharingChange,
        labelFor = { it.name },
    )

    Spacer(modifier = Modifier.height(12.dp))

    SegmentedButtonsRow(
        label = stringResource(R.string.activations_label_ad_personalization),
        entries = ConsentState.entries,
        selected = personalizationStatus,
        onSelected = onAdPersonalizationChange,
        labelFor = { it.name },
    )

    Spacer(modifier = Modifier.height(12.dp))

    SegmentedButtonsRow(
        label = stringResource(R.string.activations_label_rdp_owner),
        entries = RdpOwner.entries,
        selected = rdpOwner,
        onSelected = onRdpOwnerChange,
        labelFor = { it.name },
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = disclosureVersion,
        onValueChange = onDisclosureVersionChange,
        label = { Text(stringResource(R.string.activations_label_disclosure_version)) },
        singleLine = true,
        isError = disclosureVersionError != null,
        supportingText = { Text(disclosureVersionError ?: stringResource(R.string.activations_disclosure_version_help)) },
        modifier = Modifier.fillMaxWidth(),
    )
}
