package com.actualplatform.android.activation.development.ui.feed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `Modifier.aspectRatio` rejects a ratio of 0 with an `IllegalArgumentException`, and the feed
 * mapper defaults a missing width and a missing height to 0 independently. The card's ratio must
 * therefore stay positive for every combination the backend can send.
 */
class CardImageAspectRatioTest {

    @Test
    fun cardImageAspectRatio_widthMissingHeightPresent_fallsBackToPositiveDefault() {
        val ratio = cardImageAspectRatio(width = 0, height = 200)
        assertTrue("ratio must be positive, was $ratio", ratio > 0f)
        assertEquals(DEFAULT_CARD_IMAGE_ASPECT_RATIO, ratio, 0f)
    }

    @Test
    fun cardImageAspectRatio_heightMissing_fallsBackToPositiveDefault() {
        assertEquals(DEFAULT_CARD_IMAGE_ASPECT_RATIO, cardImageAspectRatio(width = 300, height = 0), 0f)
    }

    @Test
    fun cardImageAspectRatio_bothMissing_fallsBackToPositiveDefault() {
        assertEquals(DEFAULT_CARD_IMAGE_ASPECT_RATIO, cardImageAspectRatio(width = 0, height = 0), 0f)
    }

    @Test
    fun cardImageAspectRatio_bothPresent_isWidthOverHeight() {
        assertEquals(1.5f, cardImageAspectRatio(width = 300, height = 200), 0f)
    }
}
