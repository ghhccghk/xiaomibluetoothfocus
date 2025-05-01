package com.ghhccghk.xiaomibluetoothdiy.config

import cn.xiaowine.dsp.delegate.Delegate.serial
import cn.xiaowine.dsp.delegate.Delegate.serialLazy

class Config {
    var uiname : String by serial("focusbluestart_layout")
    var hideicon: Boolean by serialLazy(false)
}
        