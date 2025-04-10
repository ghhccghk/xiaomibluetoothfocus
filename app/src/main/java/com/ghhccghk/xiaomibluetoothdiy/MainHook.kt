package com.ghhccghk.xiaomibluetoothdiy

import cn.xiaowine.xkt.LogTool
import com.ghhccghk.xiaomibluetoothdiy.hook.Hook
import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.Log
import com.github.kyuubiran.ezxhelper.LogExtensions.logexIfThrow
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage


const val PACKAGE_NAME_HOOKED = "com.xiaomi.bluetooth"
private const val TAG = "xiaomihook"

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit /* Optional */ {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == PACKAGE_NAME_HOOKED) {
            // Init EzXHelper
            EzXHelper.initHandleLoadPackage(lpparam)
            EzXHelper.setLogTag(TAG)
            EzXHelper.setToastTag(TAG)
            LogTool.init(TAG, { BuildConfig.DEBUG })
            // Init hooks
            initHooks(Hook)
        }
    }

    // Optional
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)
    }

    private fun initHooks(vararg hook: BaseHook) {
        hook.forEach {
            runCatching {
                if (it.isInit) return@forEach
                it.init()
                it.isInit = true
                Log.i("Inited hook: ${it.name}")
            }.logexIfThrow("Failed init hook: ${it.name}")
        }
    }
}