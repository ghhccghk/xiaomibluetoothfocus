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
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.compose.material3.Icon
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNull
import com.ghhccghk.xiaomibluetoothdiy.BaseHook
import com.ghhccghk.xiaomibluetoothdiy.R
import com.ghhccghk.xiaomibluetoothdiy.utils.ResInjectTool.injectModuleRes
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.hyperfocus.api.FocusApi
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
            loadClass(a.name).methodFinder().first { name == "a" }.createHook {

                before { param ->
                    val api = FocusApi()
                    param.result = null
                    val context = param.args[0] as Context
                    val device = param.args[1] as BluetoothDevice
                    val iArr = param.args[2] as IntArray
                    val deviceName = device.name ?: "未知设备"
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

                    val focustest_layout = res.getIdentifier("focustest_layout", "layout", "com.ghhccghk.xiaomibluetoothdiy")
                    val device_name = res.getIdentifier("device_name", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val connection_status = res.getIdentifier("focus_small_title", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val right_battery = res.getIdentifier("right_battery", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val left_battery = res.getIdentifier("left_battery", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val case_battery = res.getIdentifier("case_battery", "id","com.ghhccghk.xiaomibluetoothdiy" )
                    val disconnect = res.getIdentifier("focus_button_title", "id","com.ghhccghk.xiaomibluetoothdiy" )

                    val rV = RemoteViews("com.ghhccghk.xiaomibluetoothdiy",focustest_layout)

                    rV.setTextViewText(device_name,deviceName)
                    rV.setTextViewText(connection_status,"已连接")
                    rV.setTextViewText(right_battery,"右：$right")
                    rV.setTextViewText(left_battery,"左：$left")
                    rV.setTextViewText(case_battery,"充电盒：$case")
                    rV.setTextViewText(disconnect,"断开")
                    rV.setOnClickPendingIntent(disconnect,ass)

                    val actionss = api.actionInfo(actionsIntent = intent, actionsTitle = "断开", actionTitleColor = "#FFFFFF")
                    actionss.put("actionIntentType",2)
                    actionss.toString().log()

                    val hintInfo = api.hintInfo(type = 1 ,
                        titleLineCount = 6, actionInfo = actionss,
                        title = "已连接")

                    val baseinfo = api.baseinfo(
                        content = deviceName,
                        title = "左：$left",
                        subTitle = "右：$right",
                        extraTitle = "充电盒：$case"
                    )
                    val a = android.R.drawable.stat_sys_data_bluetooth
                    val focus = api.senddiyFocus(
                        ticker = "",
                        picticker = Icon.createWithResource(context,android.R.drawable.stat_sys_data_bluetooth),
                        rv = rV,
                    )

                    val content = "耳机：$deviceName\n 左：$left | 右：$right\n 充电盒：$case"

                    // 构建你自己的通知
                    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val channelId = ("BTHeadset" + device.address)

                    val notification = Notification.Builder(context, channelId)
                        .setContentTitle(deviceName)
                        .setContentText(content)
                        .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                        .setAutoCancel(true)
                        .setContentIntent(ass)
                        .build()
                    notification.extras.putAll(focus)
                    manager.notify(channelId,10003,notification)
                }
            }
            loadClass(b.name).methodFinder().first { name == "onReceive" }.createHook {
                after { param ->
                    val context= param.args[0] as Context
                    val intent = param.args[1] as Intent
                    val action = intent.action
                    if ("com.android.bluetooth.headset.notification" == action) {
                        param.args[1] = int
                    }
                }
                before { param ->
                    val context= param.args[0] as Context
                    val intent = param.args[1] as Intent
                    val action = intent.action
                    if ("com.android.bluetooth.headset.notification" == action) {
                        intent.toString().log()

                        intent.extras.log()
                    }
                }
            }
        }
    }
}

