package com.actualplatform.android.activation.development.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.actualplatform.activation.RewardCurrencyCodePosition
import com.actualplatform.activation.RewardCurrencyImageLocation
import com.actualplatform.activation.RewardCurrencyLabelStyle
import com.actualplatform.activation.RewardCurrencyMessagingTextStyle
import com.actualplatform.activation.RewardCurrencyRounding
import com.actualplatform.android.activation.development.R

@Composable
internal fun RewardCurrencySection(
    currencyName: String,
    onCurrencyNameChange: (String) -> Unit,
    currencyCode: String,
    onCurrencyCodeChange: (String) -> Unit,
    userPayoutPercentage: String,
    onUserPayoutPercentageChange: (String) -> Unit,
    currencyPerDollar: String,
    onCurrencyPerDollarChange: (String) -> Unit,
    currencyCodePosition: RewardCurrencyCodePosition,
    onCurrencyCodePositionChange: (RewardCurrencyCodePosition) -> Unit,
    rewardCurrencyLabelStyle: RewardCurrencyLabelStyle,
    onRewardCurrencyLabelStyleChange: (RewardCurrencyLabelStyle) -> Unit,
    rewardCurrencyMessagingStyle: RewardCurrencyMessagingTextStyle,
    onRewardCurrencyMessagingStyleChange: (RewardCurrencyMessagingTextStyle) -> Unit,
    rewardRounding: RewardCurrencyRounding,
    onRewardRoundingChange: (RewardCurrencyRounding) -> Unit,
    currencyImageLocations: Set<RewardCurrencyImageLocation>,
    onCurrencyImageLocationsChange: (Set<RewardCurrencyImageLocation>) -> Unit,
) {
    Text(stringResource(R.string.activations_section_reward_currency), style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = currencyName,
        onValueChange = onCurrencyNameChange,
        label = { Text(stringResource(R.string.activations_label_currency_name)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = currencyCode,
        onValueChange = onCurrencyCodeChange,
        label = { Text(stringResource(R.string.activations_label_currency_code)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = userPayoutPercentage,
        onValueChange = { input ->
            val filtered = input.filter { c -> c.isDigit() || c == '.' }
            if (filtered.count { it == '.' } <= 1) {
                onUserPayoutPercentageChange(filtered)
            }
        },
        label = { Text(stringResource(R.string.activations_label_user_payout_percentage)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = currencyPerDollar,
        onValueChange = { onCurrencyPerDollarChange(it.filter { c -> c.isDigit() || c == '.' }) },
        label = { Text(stringResource(R.string.activations_label_currency_per_dollar)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(12.dp))

    EnumSegmentedButtonRow(
        label = stringResource(R.string.activations_label_currency_code_position),
        entries = RewardCurrencyCodePosition.entries,
        selected = currencyCodePosition,
        onSelected = onCurrencyCodePositionChange,
        labelFor = { it.name },
    )

    Spacer(modifier = Modifier.height(12.dp))

    EnumSegmentedButtonRow(
        label = stringResource(R.string.activations_label_reward_label_style),
        entries = RewardCurrencyLabelStyle.entries,
        selected = rewardCurrencyLabelStyle,
        onSelected = onRewardCurrencyLabelStyleChange,
        labelFor = { it.name },
    )

    Spacer(modifier = Modifier.height(12.dp))

    EnumSegmentedButtonRow(
        label = stringResource(R.string.activations_label_reward_messaging_style),
        entries = RewardCurrencyMessagingTextStyle.entries,
        selected = rewardCurrencyMessagingStyle,
        onSelected = onRewardCurrencyMessagingStyleChange,
        labelFor = { it.name },
    )

    Spacer(modifier = Modifier.height(12.dp))

    EnumSegmentedButtonRow(
        label = stringResource(R.string.activations_label_reward_rounding),
        entries = RewardCurrencyRounding.entries,
        selected = rewardRounding,
        onSelected = onRewardRoundingChange,
        labelFor = { it.name },
    )

    Spacer(modifier = Modifier.height(12.dp))

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.activations_label_currency_image_locations),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        RewardCurrencyImageLocation.entries.forEach { location ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = location in currencyImageLocations,
                    onCheckedChange = { checked ->
                        onCurrencyImageLocationsChange(
                            if (checked) currencyImageLocations + location
                            else currencyImageLocations - location,
                        )
                    },
                )
                Text(text = location.name, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
