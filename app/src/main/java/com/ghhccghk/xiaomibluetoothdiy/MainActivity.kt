package com.ghhccghk.xiaomibluetoothdiy

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import cn.xiaowine.dsp.DSP
import cn.xiaowine.dsp.data.MODE
import cn.xiaowine.xkt.AcTool
import cn.xiaowine.xkt.LogTool
import com.ghhccghk.xiaomibluetoothdiy.databinding.ActivityMainBinding
import com.ghhccghk.xiaomibluetoothdiy.tools.Tools.xpActivation
import com.ghhccghk.xiaomibluetoothdiy.ui.viewmodel.ShareViewModel
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private val shareViewModel: ShareViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val TAG = "xiaomihook"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AcTool.init(this)
        LogTool.init(TAG, { BuildConfig.DEBUG }, BuildConfig.DEBUG)
        enableEdgeToEdge()
        xpActivation = DSP.init(this, BuildConfig.APPLICATION_ID, MODE.HOOK, false)
        shareViewModel.activated = xpActivation
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun onStart() {
        super.onStart()
        val navView: NavigationBarView = binding.nav
        navView.setupWithNavController(findNavController(R.id.nav_host_fragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
