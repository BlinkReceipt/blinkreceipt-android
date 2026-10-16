package com.actualplatform.android.activation.development.ui

import android.content.SharedPreferences
import com.actualplatform.activation.BlinkPrivacy
import com.actualplatform.activation.ConsentState
import com.actualplatform.activation.RdpOwner

/**
 * The privacy values this app states to the activation client, stored like every other setting.
 *
 * A real host takes these from its consent flow; this app takes them from the Settings screen so
 * every combination the SDK reacts to can be tried. They are applied before the receipts SDK
 * starts and again on every save.
 */
internal data class PrivacySettings(
    val sellOrShareStatus: ConsentState,
    val personalizationStatus: ConsentState,
    /** ISO date of the notice the user saw; null when none. */
    val disclosureVersion: String?,
    val rdpOwner: RdpOwner,
) {
    fun toBlinkPrivacy(): BlinkPrivacy =
        BlinkPrivacy(
            sellOrShareStatus = sellOrShareStatus,
            personalizationStatus = personalizationStatus,
            disclosureVersion = disclosureVersion?.takeIf { it.isNotBlank() },
            rdpOwner = rdpOwner,
        )

    /**
     * The SDK's objection to these values, or null when it accepts them. The Settings screen asks
     * before storing anything: a stored value is replayed on every launch and save, so one the SDK
     * rejects must never be written. [toPrivacy] is the conversion the SDK judges; anything it
     * throws other than an objection to the value is not the value's problem and propagates.
     */
    fun rejection(
        toPrivacy: (PrivacySettings) -> BlinkPrivacy = PrivacySettings::toBlinkPrivacy,
    ): IllegalArgumentException? =
        try {
            toPrivacy(this)
            null
        } catch (e: IllegalArgumentException) {
            e
        }

    companion object {
        /**
         * Nothing resolved, and the SDK owns Google's restricted-data-processing preference, so
         * ads load restricted rather than not at all.
         */
        val DEFAULT =
            PrivacySettings(
                sellOrShareStatus = ConsentState.UNKNOWN,
                personalizationStatus = ConsentState.UNKNOWN,
                disclosureVersion = null,
                rdpOwner = RdpOwner.ACTUAL,
            )

        private val ISO_DATE = Regex("""(\d{4})-(\d{2})-(\d{2})""")

        /**
         * Empty means no notice was seen; anything else must be an ISO date that exists on the
         * calendar. Checked by hand rather than with `java.time`, which API 24-25 do not have
         * without desugaring.
         */
        fun isValidDisclosureVersion(value: String): Boolean {
            if (value.isBlank()) return true
            val (year, month, day) = ISO_DATE.matchEntire(value.trim())?.destructured ?: return false
            val monthNumber = month.toInt()
            if (monthNumber !in 1..12) return false
            return day.toInt() in 1..daysIn(year.toInt(), monthNumber)
        }

        private fun daysIn(year: Int, month: Int): Int =
            when (month) {
                2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
                4, 6, 9, 11 -> 30
                else -> 31
            }

        fun from(prefs: SharedPreferences): PrivacySettings =
            fromStored(
                sellOrShareStatus = prefs.getString(ActivationActivity.PREF_PRIVACY_SELL_OR_SHARE_STATUS, null),
                personalizationStatus = prefs.getString(ActivationActivity.PREF_PRIVACY_PERSONALIZATION_STATUS, null),
                disclosureVersion = prefs.getString(ActivationActivity.PREF_PRIVACY_DISCLOSURE_VERSION, null),
                rdpOwner = prefs.getString(ActivationActivity.PREF_PRIVACY_RDP_OWNER, null),
            )

        /** Values as stored, by [Enum.name]; a missing or unknown name falls back to [DEFAULT]. */
        fun fromStored(
            sellOrShareStatus: String?,
            personalizationStatus: String?,
            disclosureVersion: String?,
            rdpOwner: String?,
        ): PrivacySettings =
            PrivacySettings(
                sellOrShareStatus = sellOrShareStatus.toEnumOr(DEFAULT.sellOrShareStatus),
                personalizationStatus = personalizationStatus.toEnumOr(DEFAULT.personalizationStatus),
                disclosureVersion = disclosureVersion?.trim()?.takeIf { it.isNotEmpty() },
                rdpOwner = rdpOwner.toEnumOr(DEFAULT.rdpOwner),
            )

        private inline fun <reified T : Enum<T>> String?.toEnumOr(default: T): T =
            this?.let { name -> runCatching { enumValueOf<T>(name) }.getOrNull() } ?: default
    }
}
