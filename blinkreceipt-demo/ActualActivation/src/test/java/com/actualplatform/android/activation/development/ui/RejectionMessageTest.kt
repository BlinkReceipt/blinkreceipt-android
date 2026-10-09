package com.actualplatform.android.activation.development.ui

import org.junit.Assert.assertEquals
import org.junit.Test

/** What the Settings screen shows when the SDK rejects a value: always the label, and the reason when there is one. */
internal class RejectionMessageTest {

    @Test
    fun the_sdk_s_reason_follows_the_label_on_its_own_line() {
        assertEquals("Nothing was saved.\ndate is in the future", withReason("Nothing was saved.", IllegalArgumentException("date is in the future")))
    }

    @Test
    fun without_a_reason_only_the_label_is_shown() {
        assertEquals("Nothing was saved.", withReason("Nothing was saved.", IllegalArgumentException()))
    }

    @Test
    fun a_blank_reason_is_not_shown() {
        assertEquals("Nothing was saved.", withReason("Nothing was saved.", IllegalArgumentException("  ")))
    }
}
