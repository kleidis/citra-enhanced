// Copyright 2023 Citra Emulator Project
// Licensed under GPLv2 or any later version
// Refer to the license.txt file included.

package io.github.mandarin3ds.mandarin.display

import android.view.WindowManager
import io.github.mandarin3ds.mandarin.NativeLibrary
import io.github.mandarin3ds.mandarin.features.settings.model.BooleanSetting
import io.github.mandarin3ds.mandarin.features.settings.model.IntSetting
import io.github.mandarin3ds.mandarin.features.settings.model.Settings
import io.github.mandarin3ds.mandarin.features.settings.utils.SettingsFile
import io.github.mandarin3ds.mandarin.utils.EmulationMenuSettings

class ScreenAdjustmentUtil(private val windowManager: WindowManager,
                           private val settings: Settings) {
    fun swapScreen() {
        val isEnabled = !EmulationMenuSettings.swapScreens
        EmulationMenuSettings.swapScreens = isEnabled
        NativeLibrary.swapScreens(
            isEnabled,
            windowManager.defaultDisplay.rotation
        )
        BooleanSetting.SWAP_SCREEN.boolean = isEnabled
        settings.saveSetting(BooleanSetting.SWAP_SCREEN, SettingsFile.FILE_NAME_CONFIG)
    }

    fun cycleLayouts() {
        val nextLayout = (EmulationMenuSettings.landscapeScreenLayout + 1) % ScreenLayout.entries.size
        changeScreenOrientation(ScreenLayout.from(nextLayout))
    }

    fun changeScreenOrientation(layoutOption: ScreenLayout) {
        EmulationMenuSettings.landscapeScreenLayout = layoutOption.int
        NativeLibrary.notifyOrientationChange(
            EmulationMenuSettings.landscapeScreenLayout,
            windowManager.defaultDisplay.rotation
        )
        IntSetting.SCREEN_LAYOUT.int = layoutOption.int
        settings.saveSetting(IntSetting.SCREEN_LAYOUT, SettingsFile.FILE_NAME_CONFIG)
    }
}
