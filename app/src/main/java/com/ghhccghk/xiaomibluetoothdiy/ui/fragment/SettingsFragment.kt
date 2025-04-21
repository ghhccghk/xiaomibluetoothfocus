package com.ghhccghk.xiaomibluetoothdiy.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.ghhccghk.xiaomibluetoothdiy.R
import com.ghhccghk.xiaomibluetoothdiy.databinding.FragmentSettingsBinding
import com.ghhccghk.xiaomibluetoothdiy.tools.ConfigTools.config
import com.ghhccghk.xiaomibluetoothdiy.tools.Tools.dp2px
import com.ghhccghk.xiaomibluetoothdiy.ui.view.Preferences

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentSettingLinearlayout.apply {
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.hide_icon,
                    summaryResId = R.string.hide_icon_summary,
                    isChecked = config.hideicon,
                    onCheckedChange = { _, isChecked -> config.hideicon = isChecked }
                ))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun createScrollableDialogLayout(context: Context): Pair<ScrollView, LinearLayout> {
        val scrollView = ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(0, 0, 0, dp2px(context, 8f))
        }

        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        scrollView.addView(contentLayout)
        return Pair(scrollView, contentLayout)
    }

    private fun createSwitchView(
        context: Context,
        titleResId: Int,
        summaryResId: Int = 0,
        isChecked: Boolean = false,
        onCheckedChange: (CompoundButton, Boolean) -> Unit
    ): View {
        return createCustomView(
            context = context,
            titleResId = titleResId,
            summaryResId = summaryResId,
            isChecked = isChecked,
            onCheckedChange = onCheckedChange,
            hideSummary = summaryResId == 0,
            hideSwitch = false
        )
    }

    private fun createClickableView(
        context: Context,
        titleResId: Int,
        onClick: (View) -> Unit
    ): View {
        return createCustomView(
            context = context,
            titleResId = titleResId,
            onClick = onClick,
            hideSummary = true,
            hideSwitch = true
        )
    }

    private fun createCustomView(
        context: Context,
        titleResId: Int,
        summaryResId: Int = 0,
        isChecked: Boolean = false,
        onCheckedChange: ((CompoundButton, Boolean) -> Unit)? = null,
        hideSummary: Boolean = false,
        hideSwitch: Boolean = false,
        onClick: ((View) -> Unit)? = null
    ): View {
        val preferences = Preferences(context)
        preferences.setViewClickToggleSwitch()
        val switchView = preferences.getView()

        // Title
        preferences.preferencesTitle.setText(titleResId)

        // Summary
        if (hideSummary || summaryResId == 0) {
            preferences.preferencesSummary.visibility = View.GONE
        } else {
            preferences.setSummary(summaryResId)
        }

        // Switch
        if (hideSwitch || onCheckedChange == null) {
            preferences.preferencesButton.visibility = View.GONE
        } else {
            preferences.preferencesButton.isChecked = isChecked
            preferences.preferencesButton.setOnCheckedChangeListener(onCheckedChange)
        }

        // Click listener
        onClick?.let { switchView.setOnClickListener(it) }

        return switchView
    }

}