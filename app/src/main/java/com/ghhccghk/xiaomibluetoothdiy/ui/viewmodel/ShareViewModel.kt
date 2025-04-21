package com.ghhccghk.xiaomibluetoothdiy.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class ShareViewModel(private val state: SavedStateHandle) : ViewModel() {
    var activated: Boolean
        get() = state["activated"] ?: false
        set(value) {
            state["activated"] = value
        }

}