package com.actualplatform.android.activation.development.ui

import com.actualplatform.activation.ConsentState
import com.actualplatform.activation.RdpOwner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The privacy values the demo app states to the activation client are stored as strings like every
 * other setting; these pin how they are read back and validated.
 */
internal class PrivacySettingsTest {

    @Test
    fun nothing_stored_states_unresolved_answers_and_sdk_ownership() {
        val settings = PrivacySettings.fromStored(null, null, null, null)

        assertEquals(ConsentState.UNKNOWN, settings.sellOrShareStatus)
        assertEquals(ConsentState.UNKNOWN, settings.personalizationStatus)
        assertNull(settings.disclosureVersion)
        assertEquals(RdpOwner.ACTUAL, settings.rdpOwner)
    }

    @Test
    fun stored_names_are_read_back() {
        val settings =
            PrivacySettings.fromStored("NOT_OPTED_OUT", "OPTED_OUT", "2026-07-30", "PUBLISHER")

        assertEquals(ConsentState.NOT_OPTED_OUT, settings.sellOrShareStatus)
        assertEquals(ConsentState.OPTED_OUT, settings.personalizationStatus)
        assertEquals("2026-07-30", settings.disclosureVersion)
        assertEquals(RdpOwner.PUBLISHER, settings.rdpOwner)
    }

    @Test
    fun a_name_the_sdk_no_longer_knows_falls_back_to_the_default() {
        val settings = PrivacySettings.fromStored("MAYBE", "YES", null, "SOMEONE")

        assertEquals(PrivacySettings.DEFAULT, settings)
    }

    @Test
    fun a_blank_disclosure_version_means_no_notice_was_seen() {
        assertNull(PrivacySettings.fromStored(null, null, "   ", null).disclosureVersion)
        assertNull(PrivacySettings.fromStored(null, null, "", null).toBlinkPrivacy().disclosureVersion)
    }

    @Test
    fun the_disclosure_version_is_an_iso_date_or_empty() {
        assertTrue(PrivacySettings.isValidDisclosureVersion(""))
        assertTrue(PrivacySettings.isValidDisclosureVersion("2026-07-30"))
        assertFalse(PrivacySettings.isValidDisclosureVersion("v2"))
        assertFalse(PrivacySettings.isValidDisclosureVersion("30/07/2026"))
    }

    @Test
    fun the_disclosure_version_must_be_a_date_that_exists() {
        for (impossible in listOf("2026-99-99", "2026-02-31", "2026-04-31", "2026-13-01", "2026-00-10", "2026-01-00", "2026-02-29", "2100-02-29")) {
            assertFalse(impossible, PrivacySettings.isValidDisclosureVersion(impossible))
        }
        for (real in listOf("2028-02-29", "2000-02-29", "2026-12-31", "2026-01-01", " 2026-07-30 ")) {
            assertTrue(real, PrivacySettings.isValidDisclosureVersion(real))
        }
    }

    /** Pins that nothing the Settings screen can produce is rejected by the SDK as shipped. */
    @Test
    fun values_the_sdk_accepts_have_no_rejection() {
        val everyCombination =
            ConsentState.entries.flatMap { sellOrShare ->
                ConsentState.entries.flatMap { personalization ->
                    RdpOwner.entries.flatMap { owner ->
                        listOf(null, "2026-07-30").map { disclosureVersion ->
                            PrivacySettings(sellOrShare, personalization, disclosureVersion, owner)
                        }
                    }
                }
            }

        assertEquals(36, everyCombination.size)
        for (settings in everyCombination) assertNull("$settings", settings.rejection())
    }

    @Test
    fun the_sdk_s_objection_is_returned_as_the_rejection() {
        val objection = IllegalArgumentException("disclosure version is in the future")

        assertSame(objection, PrivacySettings.DEFAULT.rejection { throw objection })
    }

    /** Anything but an objection to the value is not the value's problem and must not be hidden. */
    @Test
    fun a_failure_that_is_not_an_objection_propagates() {
        val broken = IllegalStateException("broken")

        val thrown =
            assertThrows(IllegalStateException::class.java) {
                PrivacySettings.DEFAULT.rejection { throw broken }
            }

        assertSame(broken, thrown)
    }

    @Test
    fun the_client_receives_exactly_the_stored_answers() {
        val privacy =
            PrivacySettings(
                sellOrShareStatus = ConsentState.OPTED_OUT,
                personalizationStatus = ConsentState.NOT_OPTED_OUT,
                disclosureVersion = "2026-07-30",
                rdpOwner = RdpOwner.PUBLISHER,
            ).toBlinkPrivacy()

        assertEquals(ConsentState.OPTED_OUT, privacy.sellOrShareStatus)
        assertEquals(ConsentState.NOT_OPTED_OUT, privacy.personalizationStatus)
        assertEquals("2026-07-30", privacy.disclosureVersion)
        assertEquals(RdpOwner.PUBLISHER, privacy.rdpOwner)
    }
}
