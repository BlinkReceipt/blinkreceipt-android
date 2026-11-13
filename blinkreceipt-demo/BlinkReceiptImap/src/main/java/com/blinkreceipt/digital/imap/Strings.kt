package com.blinkreceipt.digital.imap

import com.actualplatform.receipt.digital.Provider
import java.util.*

fun String.toProvider(): Provider = Provider.valueOf(this.uppercase(Locale.US))