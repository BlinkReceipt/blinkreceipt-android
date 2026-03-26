package com.actualplatform.android.activation.development.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.actualplatform.android.EngageClient
import com.actualplatform.android.R

@Composable
internal fun ReceiptValidationSection(
    receiptMaxAgeDays: String,
    onReceiptMaxAgeDaysChange: (String) -> Unit,
) {
    Text(stringResource(R.string.engage_section_receipt_validation), style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = receiptMaxAgeDays,
            onValueChange = { onReceiptMaxAgeDaysChange(it.filter { c -> c.isDigit() }) },
            label = { Text(stringResource(R.string.engage_label_receipt_max_age)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(
            onClick = { onReceiptMaxAgeDaysChange(EngageClient.DEFAULT_RECEIPT_MAX_AGE_DAYS.toString()) },
        ) {
            Text(stringResource(R.string.engage_button_reset))
        }
    }
}
