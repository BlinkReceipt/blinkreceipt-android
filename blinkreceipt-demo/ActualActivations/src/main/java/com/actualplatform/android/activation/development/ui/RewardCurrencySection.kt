package com.actualplatform.android.activation.development.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.actualplatform.android.R

@Composable
internal fun RewardCurrencySection(
    currencyName: String,
    onCurrencyNameChange: (String) -> Unit,
    payoutPercentage: String,
    onPayoutPercentageChange: (String) -> Unit,
    iconBase64: String?,
    onIconBase64Change: (String?) -> Unit,
) {
    val context = LocalContext.current

    val iconPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        if (bytes != null) {
            onIconBase64Change(Base64.encodeToString(bytes, Base64.DEFAULT))
        }
    }

    Text(stringResource(R.string.engage_section_reward_currency), style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = currencyName,
        onValueChange = onCurrencyNameChange,
        label = { Text(stringResource(R.string.engage_label_currency_name)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = payoutPercentage,
        onValueChange = { onPayoutPercentageChange(it.filter { c -> c.isDigit() || c == '.' }) },
        label = { Text(stringResource(R.string.engage_label_payout_percentage)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(stringResource(R.string.engage_label_reward_icon), style = MaterialTheme.typography.bodyMedium)
    Spacer(modifier = Modifier.height(4.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        iconBase64?.let { base64 ->
            runCatching {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }.getOrNull()?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.engage_label_reward_icon),
                    modifier = Modifier.size(48.dp),
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
            }
        }

        OutlinedButton(onClick = { iconPickerLauncher.launch("image/*") }) {
            Text(stringResource(R.string.engage_action_pick_icon))
        }

        if (iconBase64 != null) {
            TextButton(onClick = { onIconBase64Change(null) }) {
                Text(stringResource(R.string.engage_action_clear_icon))
            }
        }
    }
}
