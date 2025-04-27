package com.ghhccghk.xiaomibluetoothdiy.config

import cn.xiaowine.dsp.delegate.Delegate.serialLazy

class Config {
    var hideicon: Boolean by serialLazy(true)
    var uiname : String by  serialLazy("focusbluestart_layout")
}
        