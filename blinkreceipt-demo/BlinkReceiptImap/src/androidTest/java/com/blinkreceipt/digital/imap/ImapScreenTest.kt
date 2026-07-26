package com.blinkreceipt.digital.imap

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImapScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(
        state: ImapUiState,
        requestedIntervalHours: Int = 24,
        effectiveIntervalHours: Int = 24,
        onAction: (ImapAction) -> Unit = {},
        onIntervalSelected: (Int) -> Unit = {},
        onCredentialsConfirmed: () -> Unit = {},
        onCredentialsDismissed: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            ImapTheme {
                ImapScreen(
                    state = state,
                    requestedIntervalHours = requestedIntervalHours,
                    effectiveIntervalHours = effectiveIntervalHours,
                    onAction = onAction,
                    onIntervalSelected = onIntervalSelected,
                    onCredentialsConfirmed = onCredentialsConfirmed,
                    onCredentialsDismissed = onCredentialsDismissed,
                )
            }
        }
    }

    @Test
    fun everyActionIsDisabledBeforeTheClientInitializes() {
        setScreen(ImapUiState())

        ImapAction.entries.forEach { action ->
            composeTestRule.onNodeWithTag(action.label)
                .performScrollTo()
                .assertIsNotEnabled()
        }
    }

    @Test
    fun everyActionIsEnabledOnceTheClientIsReady() {
        setScreen(ImapUiState(enabledActions = ImapAction.entries.toSet()))

        ImapAction.entries.forEach { action ->
            composeTestRule.onNodeWithTag(action.label)
                .performScrollTo()
                .assertIsEnabled()
        }
    }

    @Test
    fun onlyTheEnabledSubsetIsClickable() {
        setScreen(ImapUiState(enabledActions = setOf(ImapAction.CLEAR, ImapAction.LOGIN)))

        composeTestRule.onNodeWithTag(ImapAction.CLEAR.label)
            .performScrollTo()
            .assertIsEnabled()

        composeTestRule.onNodeWithTag(ImapAction.SINGLE_LOGOUT.label)
            .performScrollTo()
            .assertIsNotEnabled()
    }

    @Test
    fun clickingAnActionReportsIt() {
        var clicked: ImapAction? = null

        setScreen(
            state = ImapUiState(enabledActions = ImapAction.entries.toSet()),
            onAction = { clicked = it },
        )

        composeTestRule.onNodeWithTag(ImapAction.SINGLE_LOGOUT.label)
            .performScrollTo()
            .performClick()

        assertEquals(ImapAction.SINGLE_LOGOUT, clicked)
    }

    @Test
    fun resultsTextIsRendered() {
        setScreen(ImapUiState(results = "ScanResults Size: 7"))

        composeTestRule.onNodeWithTag(ResultsTestTag)
            .performScrollTo()
            .assertTextEquals("ScanResults Size: 7")
    }

    @Test
    fun credentialsDialogIsHiddenUntilRequested() {
        setScreen(ImapUiState())

        composeTestRule.onNodeWithText("Credentials").assertDoesNotExist()
    }

    @Test
    fun credentialsDialogConfirmAndDismissAreReported() {
        var confirmed = false
        var dismissed = false

        setScreen(
            state = ImapUiState(credentialsVisible = true),
            onCredentialsConfirmed = { confirmed = true },
            onCredentialsDismissed = { dismissed = true },
        )

        composeTestRule.onNodeWithText("Credentials").assertIsDisplayed()
        composeTestRule.onNodeWithText("GMAIL").assertIsDisplayed()

        composeTestRule.onNodeWithText("Cancel").performClick()
        assertTrue(dismissed)

        composeTestRule.onNodeWithText("Ok").performClick()
        assertTrue(confirmed)
    }

    @Test
    fun intervalSelectorShowsTheCurrentCadence() {
        setScreen(
            state = ImapUiState(),
            requestedIntervalHours = 48,
            effectiveIntervalHours = 48,
        )

        composeTestRule.onNodeWithTag(IntervalSelectorTestTag)
            .performScrollTo()
            .assertTextEquals("Auto-scrape every 48 h")
    }

    @Test
    fun pickingAnIntervalReportsTheChosenHours() {
        var selected: Int? = null

        setScreen(
            state = ImapUiState(),
            onIntervalSelected = { selected = it },
        )

        composeTestRule.onNodeWithTag(IntervalSelectorTestTag)
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithText("48 hours").performClick()

        assertEquals(48, selected)
    }

    @Test
    fun everyOfferedIntervalIsSelectable() {
        val selected = mutableListOf<Int>()

        setScreen(
            state = ImapUiState(),
            onIntervalSelected = { selected += it },
        )

        IntervalChoices.forEach { hours ->
            composeTestRule.onNodeWithTag(IntervalSelectorTestTag)
                .performScrollTo()
                .performClick()

            composeTestRule.onNodeWithText("$hours hours").performClick()
        }

        assertEquals(IntervalChoices, selected)
    }

    @Test
    fun noClampNoticeWhenTheChoiceIsAtOrAboveTheFloor() {
        setScreen(
            state = ImapUiState(),
            requestedIntervalHours = 12,
            effectiveIntervalHours = 12,
        )

        composeTestRule.onNodeWithTag(IntervalClampNoticeTestTag).assertDoesNotExist()
    }

    @Test
    fun clampNoticeExplainsASubFloorChoice() {
        setScreen(
            state = ImapUiState(),
            requestedIntervalHours = 6,
            effectiveIntervalHours = 12,
        )

        composeTestRule.onNodeWithTag(IntervalClampNoticeTestTag)
            .performScrollTo()
            .assertTextEquals("6 h is below the SDK minimum — it will run every 12 h")
    }
}
