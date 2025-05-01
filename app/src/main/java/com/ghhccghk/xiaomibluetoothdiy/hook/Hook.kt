package com.ghhccghk.xiaomibluetoothdiy.hook

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.compose.material3.Icon
import androidx.transition.Visibility
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNull
import com.ghhccghk.xiaomibluetoothdiy.BaseHook
import com.ghhccghk.xiaomibluetoothdiy.R
import com.ghhccghk.xiaomibluetoothdiy.tools.ConfigTools.xConfig
import com.ghhccghk.xiaomibluetoothdiy.utils.ResInjectTool.injectModuleRes
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper.classLoader
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.hyperfocus.api.FocusApi
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers.findClass
import org.json.JSONObject
import org.luckypray.dexkit.DexKitBridge
import java.text.NumberFormat


private fun getApplication(callback: (Application) -> Unit) {
    var isLoad = false
    Application::class.java.methodFinder().filterByName("attach").first().createHook {
        after {
            if (isLoad) return@after
            isLoad = true
            callback(it.thisObject as Application)
        }
    }
}

private fun dexKitBridge(classLoader: ClassLoader? = null, block: (DexKitBridge) -> Unit) {
    System.loadLibrary("dexkit")
    if (classLoader.isNull()) {
        getApplication { application ->
            DexKitBridge.create(application.classLoader, false).use {
                block(it)
            }
        }
    } else {
        DexKitBridge.create(classLoader!!, false).use {
            block(it)
        }
    }
}


object Hook : BaseHook() {
    override val name: String = "小米hook"

    init {
        System.loadLibrary("dexkit")
    }

    @SuppressLint("MissingPermission")
    override fun init() {
        super.init()

        var btData : Bundle? = null
        var int : Intent? = null
        dexKitBridge { dexKitBridge ->
            val a = dexKitBridge.findClass {
                matcher {
                    usingStrings("create HeadsetNotification")
                }
            }.single()
            val b = dexKitBridge.findClass {
                matcher {
                        usingStrings("ACTION_LE_AUDIO mPreWearStateList: ")
                }
            }.single()

            val c = dexKitBridge.findClass {
                matcher {
                    usingStrings("cancle headset anti_lost notification:")
                }
            }.single()

            val L = dexKitBridge.findClass {
                matcher {
                    usingStrings("show anti_lost notification in: ")
                }
            }.single()

            loadClass(a.name).methodFinder().first { name == "a" }.createHook {
                before { param ->
                    param.result = null
                    val context = param.args[0] as Context
                    val device = param.args[1] as BluetoothDevice
                    val iArr = param.args[2] as IntArray
                    val deviceName = device.name ?: "未知设备"
                    val Z = findClass(c.name,classLoader)
                    val ll = findClass(L.name,classLoader)
                    val res = injectModuleRes(context)

                    val bundle = Bundle()
                    bundle.putParcelable("Device", device)
                    val intent = Intent("com.android.bluetooth.headset.notification")
                    // intent.setPackage("com.xiaomi.bluetooth")
                    intent.putExtra("btData", bundle)
                    btData = bundle
                    intent.putExtra("disconnect", "1")
                    intent.setIdentifier("BTHeadset" + device.address)
                    int = intent

                    val l = iArr.getOrNull(0)
                    val r = iArr.getOrNull(1)
                    val c = iArr.getOrNull(2)

                    // 你自己的电量内容
                    val left = if (l in 0..100) {
                        NumberFormat.getPercentInstance().format(l?.div(100.0) ?: 0)
                    } else {
                        "-"
                    }
                    val right = if (r in 0..100) {
                        NumberFormat.getPercentInstance().format(r?.div(100.0) ?: 0)
                    } else {
                        "-"
                    }
                    val case = if (c in 0..100) {
                        NumberFormat.getPercentInstance().format(c?.div(100.0) ?: 0)
                    } else {
                        "-"
                    }

                    val ass =PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val focustest_layout = res.getIdentifier(xConfig.uiname, "layout", "com.ghhccghk.xiaomibluetoothdiy")
                    val kl = res.getIdentifier("kl","drawable","com.ghhccghk.xiaomibluetoothdiy")
                    val imagelogo = res.getIdentifier("imagelogo", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val device_name = res.getIdentifier("device_name", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val connection_status = res.getIdentifier("focus_small_title", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val right_battery = res.getIdentifier("right_battery", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val left_battery = res.getIdentifier("left_battery", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val case_battery = res.getIdentifier("case_battery", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val disconnect = res.getIdentifier("focus_button_title", "id","com.ghhccghk.xiaomibluetoothdiy" )

                    val rV = RemoteViews("com.ghhccghk.xiaomibluetoothdiy",focustest_layout)

                    rV.setTextViewText(device_name,deviceName)
                    if (deviceName.contains("Redmi AirDots 3 Pro 原神版") ){
                        rV.setImageViewResource(imagelogo,kl)
                    }
                    if (xConfig.hideicon || xConfig.uiname != "focusbluestart_layout"){
                        rV.setViewVisibility(imagelogo, View.GONE)
                    }
                    rV.setTextViewText(connection_status,"已连接")
                    rV.setTextViewText(right_battery,": $right")
                    rV.setTextViewText(left_battery,": $left")
                    rV.setTextViewText(case_battery,": $case")
                    rV.setOnClickPendingIntent(disconnect,ass)

                    val actionss = FocusApi.actionInfo(actionIntent = intent, actionTitle = "断开", actionTitleColor = "#FFFFFF")
                    actionss.put("actionIntentType",2)
                    actionss.toString().log()

                    val focus = FocusApi.senddiyFocus(
                        ticker = "",
                        picticker = Icon.createWithResource(context,android.R.drawable.stat_sys_data_bluetooth),
                        rv = rV,
                    )

                    val content = "耳机：$deviceName\n 左：$left | 右：$right\n 充电盒：$case"

                    // 构建你自己的通知
                    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val channelId = ("BTHeadset" + device.address)

                    val method = Z.getDeclaredMethod("c", Context::class.java, String::class.java)
                    val result = method.invoke(null, context, device.address) as? String

                    val methoda = ll.getDeclaredMethod("e0", Context::class.java, BluetoothDevice::class.java)
                    val resulta = methoda.invoke(null, context, device) as? BluetoothDevice


                    val intent2 = Intent("com.android.bluetooth.headset.click.detail_notification").apply {
                        putExtra("bluetoothaddress", device.address)
                        putExtra("COME_FROM", "MIUI_BLUETOOTH_SETTINGS")
                        putExtra("MIUI_HEADSET_SUPPORT", result)
                        putExtra("android.bluetooth.device.extra.DEVICE", resulta)
                        setIdentifier("BTHeadset" + device.address)
                    }

                    val broadcast = PendingIntent.getBroadcast(
                        context,
                        0,
                        intent2,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )


                    val notification = Notification.Builder(context, channelId)
                        .setContentTitle(deviceName)
                        .setContentText(content)
                        .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                        .setAutoCancel(true)
                        .setContentIntent(broadcast)
                        .build()
                    notification.extras.putAll(focus)
                    manager.notify(channelId,10003,notification)
                }
            }
            loadClass(b.name).methodFinder().first { name == "onReceive" }.createHook {
                before { param ->
                    val context= param.args[0] as Context
                    val intent = param.args[1] as Intent
                    val action = intent.action
                    if ("com.android.bluetooth.headset.click.detail_notification" == action) {
                        intent.toString().log()
                        intent.extras.log()
                        intent.extras?.get("android.bluetooth.device.extra.DEVICE").log()
                        intent.extras?.get("MIUI_HEADSET_SUPPORT").log()
                    }
                }
            }
        }
    }
}

