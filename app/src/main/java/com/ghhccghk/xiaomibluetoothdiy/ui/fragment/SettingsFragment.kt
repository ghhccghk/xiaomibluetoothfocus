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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cn.xiaowine.dsp.DSP
import cn.xiaowine.dsp.data.MODE
import cn.xiaowine.xkt.LogTool.log
import com.ghhccghk.xiaomibluetoothdiy.BuildConfig
import com.ghhccghk.xiaomibluetoothdiy.R
import com.ghhccghk.xiaomibluetoothdiy.databinding.FragmentSettingsBinding
import com.ghhccghk.xiaomibluetoothdiy.tools.ConfigTools.config
import com.ghhccghk.xiaomibluetoothdiy.tools.LayoutIdService
import com.ghhccghk.xiaomibluetoothdiy.tools.Tools.dp2px
import com.ghhccghk.xiaomibluetoothdiy.ui.view.Preferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    private var addview : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var TestLayout = binding.fragmentSettingLinearlayout.findViewById<LinearLayout>(R.id.home_ui_test_layout)

        context?.let { uiset(config.uiname,it,TestLayout) }

        binding.fragmentSettingLinearlayout.apply {
            addView(
                createCustomOptionView(
                    context = context,
                    titleResId = R.string.uiset,
                    summary = R.string.hide_icon_summary,
                    options = listOf("focusbluestart_layout", "blueonlybattery_layout", "bluebatteryandname_layout"),
                    selectedOption = config.uiname,
                    onOptionSelected = { selectedText, _ ->
                        config.uiname = selectedText
                        context?.let { uiset(config.uiname, it, TestLayout) }
                    }
                )
            )
            addView(
                createSwitchView(
                    context = context,
                    titleResId = R.string.hide_icon,
                    summaryResId = R.string.hide_icon_summary,
                    isChecked = config.hideicon,
                    onCheckedChange = { _, isChecked ->
                        config.hideicon = isChecked
                        context?.let { uiset(config.uiname, it, TestLayout) }
                    }
                )
            )
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        addview = false
    }


    private fun createSwitchView(
        context: Context,
        titleResId: Int = 0,
        title: String? = null,
        summaryResId: Int = 0,
        summary: String? = null,
        isChecked: Boolean = false,
        onCheckedChange: (CompoundButton, Boolean) -> Unit
    ): View {
        return createCustomView(
            context = context,
            titleResId = titleResId,
            title = title,
            summary = summary,
            summaryResId = summaryResId,
            isChecked = isChecked,
            onCheckedChange = onCheckedChange,
            hideSummary = summaryResId == 0,
            hideSwitch = false
        )
    }

    private fun createClickableView(
        context: Context,
        titleResId: Int = 0,
        title: String? = null,
        onClick: (View) -> Unit
    ): View {
        return createCustomView(
            context = context,
            titleResId = titleResId,
            title = title,
            onClick = onClick,
            hideSummary = true,
            hideSwitch = true,
        )
    }

    private fun createCustomView(
        context: Context,
        titleResId: Int = 0,
        title: String? = null,
        summary: String? = null,
        summaryResId: Int = 0,
        isChecked: Boolean = false,
        onCheckedChange: ((CompoundButton, Boolean) -> Unit)? = null,
        hideSummary: Boolean = false,
        hideSwitch: Boolean = false,
        onClick: ((View) -> Unit)? = null
    ): View {
        val preferences = Preferences(context)
        val switchView = preferences.getView()

        // Title: 优先使用 titleResId，如果 title 为 null，则使用 titleResId，否则使用 title
        preferences.preferencesTitle.text = title ?: context.getString(titleResId)

        // Summary: 判断是否需要隐藏摘要，合并摘要处理
        preferences.preferencesSummary.visibility = if (hideSummary || summaryResId == 0) {
            View.GONE
        } else {
            preferences.setSummary(summary ?: context.getString(summaryResId))
            View.VISIBLE
        }

        // Switch: 根据 hideSwitch 和 onCheckedChange 决定是否显示和设置开关
        if (hideSwitch || onCheckedChange == null) {
            preferences.preferencesButton.visibility = View.GONE
        } else {
            preferences.preferencesButton.isChecked = isChecked
            preferences.preferencesButton.setOnCheckedChangeListener { buttonView, isChecked ->
                onCheckedChange.invoke(buttonView, isChecked)
            }
            preferences.setViewClickToggleSwitch() // 保持原有的点击切换功能
        }

        // Click listener
        onClick?.let { switchView.setOnClickListener(it) }

        return switchView
    }

    private fun createCustomOptionView(
        context: Context,
        titleResId: Int,
        summary: Int,
        options: List<String>,
        selectedOption: String? = null,
        onOptionSelected: (selectedText: String, selectedIndex: Int) -> Unit,
    ): View {

        val switchView =  createCustomView(context = context, titleResId = titleResId, summaryResId = summary, hideSummary = true, hideSwitch = true)

        // 点击弹出选项选择
        switchView.setOnClickListener {
            a(context, titleResId, options,onOptionSelected)
        }

        return switchView
    }


    fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }


    /** 配置蓝牙测试ui*/
    fun uiset(layoutname: String = "focusbluestart_layout",context: Context,ui: LinearLayout) {

        val layoutId = LayoutIdService.getLayoutId(context, layoutname)

        ui.removeAllViews()

        val newLayout = LayoutInflater.from(context).inflate(layoutId, ui, false)

        ui.addView(newLayout)

        val layoutBtStatus = newLayout.findViewById<LinearLayout?>(R.id.layout_bt_status)
        val imagelogo = layoutBtStatus?.findViewById<ImageButton?>(R.id.imagelogo)

        imagelogo?.visibility = if (config.hideicon) View.GONE else View.VISIBLE

    }

    fun a(context: Context,
          titleResId: Int,
          options: List<String>,
          onOptionSelected: (selectedText: String, selectedIndex: Int) -> Unit){

        val currentIndex = config.uiname.let { options.indexOf(it) }
        currentIndex.log()

        MaterialAlertDialogBuilder(context)
            .setTitle(titleResId)
            .setSingleChoiceItems(options.toTypedArray(), currentIndex) { dialog, which ->
                val selectedText = options[which]
                onOptionSelected(selectedText, which)
                dialog.dismiss()
            }
            .show()

    }

}