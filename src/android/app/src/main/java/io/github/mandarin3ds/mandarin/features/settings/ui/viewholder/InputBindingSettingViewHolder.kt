// Copyright 2023 Citra Emulator Project
// Licensed under GPLv2 or any later version
// Refer to the license.txt file included.

package io.github.mandarin3ds.mandarin.features.settings.ui.viewholder

import android.view.View
import androidx.preference.PreferenceManager
import io.github.mandarin3ds.mandarin.MandarinApplication
import io.github.mandarin3ds.mandarin.databinding.ListItemSettingBinding
import io.github.mandarin3ds.mandarin.features.settings.model.view.InputBindingSetting
import io.github.mandarin3ds.mandarin.features.settings.model.view.SettingsItem
import io.github.mandarin3ds.mandarin.features.settings.ui.SettingsAdapter

class InputBindingSettingViewHolder(val binding: ListItemSettingBinding, adapter: SettingsAdapter) :
    SettingViewHolder(binding.root, adapter) {
    private lateinit var setting: InputBindingSetting

    override fun bind(item: SettingsItem) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(MandarinApplication.appContext)
        setting = item as InputBindingSetting
        binding.textSettingName.setText(item.nameId)
        val uiString = preferences.getString(setting.abstractSetting.key, "")!!
        if (uiString.isNotEmpty()) {
            binding.textSettingDescription.visibility = View.GONE
            binding.textSettingValue.visibility = View.VISIBLE
            binding.textSettingValue.text = uiString
        } else {
            binding.textSettingDescription.visibility = View.GONE
            binding.textSettingValue.visibility = View.GONE
        }

        if (setting.isEditable) {
            binding.textSettingName.alpha = 1f
            binding.textSettingDescription.alpha = 1f
            binding.textSettingValue.alpha = 1f
        } else {
            binding.textSettingName.alpha = 0.5f
            binding.textSettingDescription.alpha = 0.5f
            binding.textSettingValue.alpha = 0.5f
        }
    }

    override fun onClick(clicked: View) {
        if (setting.isEditable) {
            adapter.onInputBindingClick(setting, bindingAdapterPosition)
        } else {
            adapter.onClickDisabledSetting()
        }
    }

    override fun onLongClick(clicked: View): Boolean {
        if (setting.isEditable) {
            adapter.onLongClick(setting.setting!!, bindingAdapterPosition)
        } else {
            adapter.onClickDisabledSetting()
        }
        return false
    }
}
