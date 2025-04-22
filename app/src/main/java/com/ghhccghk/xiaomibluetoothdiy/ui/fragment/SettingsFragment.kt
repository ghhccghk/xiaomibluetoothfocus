package com.ghhccghk.xiaomibluetoothdiy.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ghhccghk.xiaomibluetoothdiy.R
import com.ghhccghk.xiaomibluetoothdiy.databinding.FragmentSettingsBinding
import com.ghhccghk.xiaomibluetoothdiy.tools.ConfigTools.config
import com.ghhccghk.xiaomibluetoothdiy.tools.Tools.dp2px
import com.ghhccghk.xiaomibluetoothdiy.ui.view.Preferences
import com.google.android.material.textview.MaterialTextView

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

        // 创建父布局 LinearLayout
        val parentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // 创建标题 TextView
        val titleTextView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = dpToPx(22)      // 上方间隙 12dp
                bottomMargin = dpToPx(10)   // 底部间隙 4dp
            }
            setText(R.string.蓝牙通知预览)
        }
        // 添加标题 TextView 到父布局
        parentLayout.addView(titleTextView)

        // 创建外层 LinearLayout
        val homeUiTestLayout = LinearLayout(context).apply {
            id = View.generateViewId()
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4)) // 4dp paddingBottom
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(10)
            }
            background = ContextCompat.getDrawable(context, R.drawable.focus_background)
        }

    // 动态加载并添加 focustest_layout
        val includedView = LayoutInflater.from(context).inflate(R.layout.focustest_layout, homeUiTestLayout, false)
        homeUiTestLayout.addView(includedView)

        // 如果需要将 homeUiTestLayout 添加到 parentLayout
        parentLayout.addView(homeUiTestLayout)

        // 设置 includedView 的左右间隙 (margin)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            // 设置左右 margin 以提供空隙
            leftMargin = dpToPx(18) // 左侧间隙
            rightMargin = dpToPx(18) // 右侧间隙
        }

// 将修改后的 layoutParams 应用到 includedView
        homeUiTestLayout.layoutParams = params


        val b = homeUiTestLayout.findViewById<LinearLayout>(R.id.layout_bt_status)
        val imagelogo = b.findViewById<ImageButton>(R.id.imagelogo)

        binding.fragmentSettingLinearlayout.apply {
            addView(parentLayout)
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.hide_icon,
                    summaryResId = R.string.hide_icon_summary,
                    isChecked = config.hideicon,
                    onCheckedChange = { _, isChecked ->
                        config.hideicon = isChecked
                        if (isChecked){
                            imagelogo.visibility = View.GONE
                        } else {
                            imagelogo.visibility = View.VISIBLE
                        }
                    }
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

    fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }


}