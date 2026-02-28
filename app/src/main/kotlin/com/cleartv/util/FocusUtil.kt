package com.cleartv.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

/**
 * D-pad traversal helpers for Fire Stick remote navigation.
 *
 * The Fire Stick remote sends standard Android KeyEvent codes:
 * - DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT
 * - DPAD_CENTER (select)
 * - BACK
 */
object FocusUtil {

    /**
     * Modifier that handles D-pad key events and moves focus accordingly.
     * This is a fallback for cases where the TV components don't handle
     * focus traversal automatically.
     */
    fun Modifier.handleDpadNavigation(focusManager: FocusManager): Modifier =
        this.onPreviewKeyEvent { keyEvent ->
            if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

            when (keyEvent.key) {
                Key.DirectionUp -> {
                    focusManager.moveFocus(FocusDirection.Up)
                    true
                }
                Key.DirectionDown -> {
                    focusManager.moveFocus(FocusDirection.Down)
                    true
                }
                Key.DirectionLeft -> {
                    focusManager.moveFocus(FocusDirection.Left)
                    true
                }
                Key.DirectionRight -> {
                    focusManager.moveFocus(FocusDirection.Right)
                    true
                }
                else -> false
            }
        }
}
