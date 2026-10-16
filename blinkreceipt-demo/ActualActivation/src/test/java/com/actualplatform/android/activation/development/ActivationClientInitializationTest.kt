package com.actualplatform.android.activation.development

import com.actualplatform.activation.BlinkPrivacy
import com.actualplatform.activation.ConsentState
import com.actualplatform.activation.RdpOwner
import com.actualplatform.android.activation.development.ui.ActivationActivity
import com.actualplatform.android.activation.development.ui.PrivacySettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * The activation client receives the privacy values saved in Settings at launch and on every
 * save. A stored value the SDK rejects is replayed each time, so it must never take the app down
 * and leave Settings, where it gets corrected, unreachable.
 */
internal class ActivationClientInitializationTest {

    private val stored =
        PrivacySettings(
            sellOrShareStatus = ConsentState.OPTED_OUT,
            personalizationStatus = ConsentState.NOT_OPTED_OUT,
            disclosureVersion = "2026-07-30",
            rdpOwner = RdpOwner.PUBLISHER,
        )

    private val converted = mutableListOf<PrivacySettings>()

    private fun rejecting(vararg rejected: PrivacySettings): (PrivacySettings) -> BlinkPrivacy = { settings ->
        converted += settings
        require(settings !in rejected) { "rejected" }
        settings.toBlinkPrivacy()
    }

    @Test
    fun the_stored_values_are_used_when_the_sdk_accepts_them() {
        val privacy = ActivationActivity.privacyOrDefault(stored, rejecting())

        assertEquals(stored.toBlinkPrivacy(), privacy)
        assertEquals(listOf(stored), converted)
    }

    @Test
    fun stored_values_the_sdk_rejects_are_replaced_by_the_unresolved_defaults() {
        val privacy = ActivationActivity.privacyOrDefault(stored, rejecting(stored))

        assertEquals(PrivacySettings.DEFAULT.toBlinkPrivacy(), privacy)
        assertEquals(listOf(stored, PrivacySettings.DEFAULT), converted)
    }

    /** Rejecting the defaults means the SDK itself is broken, which must not be hidden. */
    @Test
    fun the_defaults_being_rejected_propagates() {
        assertThrows(IllegalArgumentException::class.java) {
            ActivationActivity.privacyOrDefault(stored, rejecting(stored, PrivacySettings.DEFAULT))
        }

        assertEquals(listOf(stored, PrivacySettings.DEFAULT), converted)
    }

    /** Only a rejected value is recovered from; any other failure is not the stored value's. */
    @Test
    fun a_failure_that_is_not_a_rejected_value_propagates_at_once() {
        val broken = IllegalStateException("broken")

        val thrown =
            assertThrows(IllegalStateException::class.java) {
                ActivationActivity.privacyOrDefault(stored) { throw broken }
            }

        assertSame(broken, thrown)
    }
}
