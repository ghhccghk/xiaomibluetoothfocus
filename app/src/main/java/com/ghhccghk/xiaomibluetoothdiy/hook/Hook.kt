package com.ghhccghk.xiaomibluetoothdiy.hook

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNotNull
import com.ghhccghk.xiaomibluetoothdiy.BaseHook
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.hyperfocus.api.FocusApi
import java.text.NumberFormat


object Hook : BaseHook() {
    override val name: String = "小米hook"
    @SuppressLint("MissingPermission", "PrivateApi")
    override fun init() {
        super.init()
        loadClassOrNull("com.android.bluetooth.ble.app.headset.b0").isNotNull {
            it.methodFinder().first { name == "a" }.createHook {
                before  @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT) { param ->
                    val api = FocusApi()
                    param.result = null
                    val context = param.args[0] as Context
                    val device = param.args[1] as BluetoothDevice
                    val iArr = param.args[2] as IntArray

                    val deviceName = device.name ?: "未知设备"

                    val bundle = Bundle()
                    bundle.putParcelable("Device", device)


                    val intent = Intent("com.android.bluetooth.headset.notification")
                    intent.putExtra("btData", bundle)
                    intent.putExtra("disconnect", "1")
                    intent.setIdentifier("BTHeadset" + device.address)

                    val l = iArr.getOrNull(0)
                    val r = iArr.getOrNull(1)
                    val C = iArr.getOrNull(2)

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
                    val case = if (C in 0..100) {
                        NumberFormat.getPercentInstance().format(C?.div(100.0) ?: 0)
                    } else {
                        "-"
                    }

                    val actions = api.actionInfo(actionsIntent = intent, actionsTitle = "点此断开", actionTitleColor = "#FFFFFF")
                    actions.toString().log()

                    val hintInfo = api.hintInfo(type = 1 ,
                        titleLineCount = 6,
                        title = "已经连接蓝牙耳机", colortitle = "#FFFFFF" ,
                        actionInfo = actions)

                    val baseinfo = api.baseinfo(
                        content = "耳机名称",
                        subContent = deviceName,
                        title = "左耳：$left",
                        subTitle = "右耳：$right",
                        extraTitle = "充电盒：$case"
                    )
                    val a = android.R.drawable.stat_sys_data_bluetooth
                    val focus = api.sendFocus(
                        title = "",
                        ticker = "",
                        baseInfo = baseinfo,
                        //hintInfo = hintInfo,
                        picticker = Icon.createWithResource(context,a),
                    )


                    val content = "耳机：$deviceName\n 左耳：$left | 右耳：$right\n 充电盒：$case"

                    // 构建你自己的通知
                    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val channelId = ("BTHeadset" + device.address)

                    val notification = Notification.Builder(context, channelId)
                        .setContentTitle(deviceName)
                        .setContentText(content)
                        .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                        .setAutoCancel(true)
                        .build()
                    notification.extras.putAll(focus)
                    manager.notify(channelId,10003,notification)
                }
            }
        }
    }
}
