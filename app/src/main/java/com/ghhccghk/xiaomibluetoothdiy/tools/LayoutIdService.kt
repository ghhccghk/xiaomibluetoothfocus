package com.ghhccghk.xiaomibluetoothdiy.tools

import android.annotation.SuppressLint
import android.content.Context

object LayoutIdService {

    private val cache = mutableMapOf<String, Int>()

    @SuppressLint("DiscouragedApi")
    fun getLayoutId(context: Context, layoutName: String): Int {
        // 先从缓存拿
        cache[layoutName]?.let { return it }

        // 缓存没有，查找
        val layoutId = context.resources.getIdentifier(layoutName, "layout", context.packageName)

        // 如果找到，缓存起来
        if (layoutId != 0) {
            cache[layoutName] = layoutId
        }

        return layoutId
    }
}
