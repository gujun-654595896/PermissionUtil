package com.gujun.permission

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gujun.permission.utils.PermissionCheckUtil
import kotlinx.android.synthetic.main.activity_permission.*

/**
 *    author : gujun
 *    date   : 2020/8/26 14:05
 *    desc   : 权限使用页面
 */
open class PermissionActivity : AppCompatActivity() {
    private val requestCode = 0x1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        btn.setOnClickListener { getPermission() }
    }

    private fun getPermission() {
        val hasPermission = PermissionCheckUtil.checkPermissions(
            this, requestCode,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
        )
        if (hasPermission)
            Toast.makeText(this, "权限全部通过", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode) {
            PermissionCheckUtil.onRequestPermissionsResult(
                this,
                requestCode,
                permissions,
                grantResults,
                "需要录音和拍照权限"
            )
        }
    }

}