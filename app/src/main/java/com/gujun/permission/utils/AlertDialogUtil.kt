package com.gujun.permission.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface

object AlertDialogUtil {

    fun showTwoButtonDialog(
        activity: Activity?,
        leftButtonStr: String,
        rightButtonStr: String,
        permissionContent: String?,
        l: DialogTwoListener?
    ) {
        AlertDialog.Builder(activity)
            .setPositiveButton(
                rightButtonStr
            ) { dialog, _ -> l?.onClickRight(dialog) }
            .setNegativeButton(
                leftButtonStr
            ) { dialog, _ -> l?.onClickLeft(dialog) }
            .setMessage(permissionContent)
            .create()
            .show()
    }

    interface DialogTwoListener {
        fun onClickLeft(dialog: DialogInterface?)
        fun onClickRight(dialog: DialogInterface?)
    }
}