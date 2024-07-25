// Copyright 2023 Citra Emulator Project
// Licensed under GPLv2 or any later version
// Refer to the license.txt file included.

package io.github.mandarin3ds.mandarin.features.hotkeys

import android.content.Context
import android.widget.Toast
import io.github.mandarin3ds.mandarin.NativeLibrary
import io.github.mandarin3ds.mandarin.R
import io.github.mandarin3ds.mandarin.utils.EmulationLifecycleUtil
import io.github.mandarin3ds.mandarin.display.ScreenAdjustmentUtil

class HotkeyUtility(private val screenAdjustmentUtil: ScreenAdjustmentUtil, private val context: Context) {

    val hotkeyButtons = Hotkey.entries.map { it.button }

    fun handleHotkey(bindedButton: Int): Boolean {
        if(hotkeyButtons.contains(bindedButton)) {
            when (bindedButton) {
                Hotkey.SWAP_SCREEN.button -> screenAdjustmentUtil.swapScreen()
                Hotkey.CYCLE_LAYOUT.button -> screenAdjustmentUtil.cycleLayouts()
                Hotkey.CLOSE_GAME.button -> EmulationLifecycleUtil.closeGame()
                Hotkey.PAUSE_OR_RESUME.button -> EmulationLifecycleUtil.pauseOrResume()
                Hotkey.QUICKSAVE.button -> {
                    NativeLibrary.saveState(NativeLibrary.QUICKSAVE_SLOT)
                    Toast.makeText(context,
                        context.getString(R.string.quicksave_saving),
                        Toast.LENGTH_SHORT).show()
                }
                Hotkey.QUICKLOAD.button -> {
                    val wasLoaded = NativeLibrary.loadStateIfAvailable(NativeLibrary.QUICKSAVE_SLOT)
                    val stringRes = if(wasLoaded) {
                        R.string.quickload_loading
                    } else {
                        R.string.quickload_not_found
                    }
                    Toast.makeText(context,
                        context.getString(stringRes),
                        Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
            return true
        }
        return false
    }
}
