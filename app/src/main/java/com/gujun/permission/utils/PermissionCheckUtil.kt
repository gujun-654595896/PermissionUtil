package com.gujun.permission.utils

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import java.util.*

object PermissionCheckUtil {

    /**
     * 检测权限，当需要提示申请权限的对话框时，调用此方法
     *
     * @param activity          页面
     * @param permissions       需要的权限列表
     * @param requestCode       ActivityCompat.requestPermissions申请系统权限时使用
     * @param permissionContent 申请权限的对话框的权限内容描述
     * @return true：低于6.0或需要的权限都允许了 ; false：反之
     */
    fun checkPermissions(
        activity: Activity?,
        requestCode: Int,
        permissions: Array<String>
    ): Boolean {
        //6.0之下的版本默认权限已开,只需要再Manifest中配置
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        if (activity == null || permissions.isEmpty()) return true

        //待申请权限列表
        val needRequestList: MutableList<String> =
            ArrayList()
        //是否需要展示申请权限的dialog
        var needShowDialog = false

        //过滤出没有申请的权限
        for (permission in permissions) {
            if (TextUtils.isEmpty(permission)) continue
            //检测是否是已申请过此权限 （PERMISSION_GRANTED：已申请），没申请的加入待申请列表
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needRequestList.add(permission)
                //检测是否需要弹App设置的dialog
                //shouldShowRequestPermissionRationale四种情况:
                //1、首次申请权限的时候 = false,此时需要调用ActivityCompat.requestPermissions调起系统的权限弹框
                //2、允许权限后再申请权限时 = false,此时就需要处理onRequestPermissionsResult回调
                //3、禁止权限，但是不勾选“不再提醒” = true,此时可以弹APP内部实现的dialog，用户点击确定时再调用ActivityCompat.requestPermissions；也可以直接继续调用ActivityCompat.requestPermissions弹系统框让用户操作用户操作
                //4、禁止权限，并勾选“不再提醒” = false,此时就需要处理onRequestPermissionsResult回调，判断有被禁止的就引导用户去系统设置的权限管理页面打开权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    needShowDialog = true
                }
            }
        }
        if (needRequestList.size > 0) {
            //有未被允许的权限
            if (needShowDialog) {
                //当未勾选“不再提醒”，调用ActivityCompat.requestPermissions
                //弹出系统弹框让用户去操作后onRequestPermissionsResult才回调
                ActivityCompat.requestPermissions(
                    activity,
                    needRequestList.toTypedArray(),
                    requestCode
                )
            } else {
                //当勾选了“不再提醒”或首次申请权限时，调用ActivityCompat.requestPermissions
                //不会弹出系统弹框直接返回onRequestPermissionsResult的回调
                ActivityCompat.requestPermissions(
                    activity,
                    needRequestList.toTypedArray(),
                    requestCode
                )
            }
            return false;
        } else {
            //权限都被允许了
            return true;
        }

    }

    /**
     * ActivityCompat.requestPermissions对应的结果回调
     *
     * @param activity          页面
     * @param requestCode       进入系统设置的权限管理页面时使用
     * @param permissions       需要的权限列表
     * @param grantResults      权限列表对应的结果（允许/拒绝）
     * @param permissionContent 申请权限的对话框的权限内容描述
     * @return true：需要的权限都允许了；false：反之
     */
    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        permissionContent: String?
    ): Boolean {
        //是否包含被禁止的
        var containRefused = false
        val length = grantResults.size
        for (i in 0 until length) {
            val grandResult = grantResults[i]
            //如果权限被禁止
            if (grandResult == PackageManager.PERMISSION_DENIED) {
                containRefused = true
            }
        }
        //只要包含被禁止的权限就提示去设置页面
        if (containRefused) {
            AlertDialogUtil.showTwoButtonDialog(
                activity,
                "取消",
                "去设置",
                permissionContent,
                object : AlertDialogUtil.DialogTwoListener {
                    override fun onClickLeft(dialog: DialogInterface?) {
                        dialog?.dismiss()
                    }

                    override fun onClickRight(dialog: DialogInterface?) {
                        dialog?.dismiss()
                        settingPermissionActivity(activity, requestCode)
                    }
                })
            return false
        }
        return true
    }

    /**
     * 进入系统设置的权限管理页面
     * @param activity      页面
     * @param requestCode   进入系统设置的权限管理页面时使用
     */
    fun settingPermissionActivity(
        activity: Activity,
        requestCode: Int
    ) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri =
            Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivityForResult(intent, requestCode)
    }
}


