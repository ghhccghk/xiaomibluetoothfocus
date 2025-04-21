package com.ghhccghk.xiaomibluetoothdiy.tools

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.TypedValue

object Tools {
    var xpActivation: Boolean = false

    val getPhoneName by lazy {
        val marketName = getSystemProperties("ro.product.marketname")
        val vivomarketName = getSystemProperties("ro.vivo.market.name")
        if (bigtextone(Build.BRAND) =="Vivo"){
            bigtextone(vivomarketName)
        } else{
            if (marketName.isNotEmpty()) bigtextone(marketName) else bigtextone(Build.BRAND) + " " + Build.MODEL
        }
    }

    fun bigtextone(st:String): String {
        val formattedBrand = st.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        return formattedBrand
    }

    @SuppressLint("PrivateApi")

    fun getSystemProperties(key: String): String {
        val ret: String = try {
            Class.forName("android.os.SystemProperties").getDeclaredMethod("get", String::class.java).invoke(null, key) as String
        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            ""
        }
        return ret
    }

    fun dp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            context.resources.displayMetrics
        ).toInt()
    }
}