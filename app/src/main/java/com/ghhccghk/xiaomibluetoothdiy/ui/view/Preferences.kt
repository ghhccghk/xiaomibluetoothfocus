package com.ghhccghk.xiaomibluetoothdiy.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.annotation.StringRes
import com.ghhccghk.xiaomibluetoothdiy.databinding.ItemsMd3PreferencesBinding
import com.google.android.material.materialswitch.MaterialSwitch

class Preferences(context: Context) {
    // 使用 ViewBinding
    private val binding: ItemsMd3PreferencesBinding =
        ItemsMd3PreferencesBinding.inflate(LayoutInflater.from(context))

    val preferencesButton: MaterialSwitch = binding.switchButton
    val preferencesTitle: TextView = binding.switchTitle
    val preferencesSummary: TextView = binding.switchSummary

    fun getView(): View = binding.root

    fun setTitle(title: String) {
        preferencesTitle.text = title
    }

    fun setSwitchChecked(isChecked: Boolean) {
        preferencesButton.isChecked = isChecked
    }

    fun setSwitchListener(listener: CompoundButton.OnCheckedChangeListener) {
        preferencesButton.setOnCheckedChangeListener(listener)
    }

    fun setViewClickToggleSwitch() {
        binding.root.setOnClickListener {
            preferencesButton.isChecked = !preferencesButton.isChecked
        }
    }

    fun setSummary(summary: String?) {
        if (summary.isNullOrBlank()) {
            preferencesSummary.visibility = View.GONE
        } else {
            preferencesSummary.visibility = View.VISIBLE
            preferencesSummary.text = summary
        }
    }

    fun setSummary(@StringRes resId: Int) {
        setSummary(binding.root.context.getString(resId))
    }
}
