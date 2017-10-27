package com.aznstudio.namaz

import android.app.Activity
import android.support.v4.app.ActivityCompat
import android.app.Fragment
import android.content.Context
import android.support.v4.content.PermissionChecker
import com.aznstudio.namaz.fragments.KiblaFragment


object PermissionHelper {

    fun isPermissionAllowedAndRequest(activity: Activity, vararg permissions: String): Boolean {
        var permissionAllowed = true

        for (permission in permissions) {
            permissionAllowed = permissionAllowed and (PermissionChecker.checkSelfPermission(activity,
                    permission) == PermissionChecker.PERMISSION_GRANTED)
        }

        if (!permissionAllowed)
            ActivityCompat.requestPermissions(activity, permissions, 0)
        return !permissionAllowed
    }

    fun showRequestRationale(fragment: Fragment, vararg permissions: String): Boolean {
        var rationale = true
        for (permission in permissions)
            rationale = rationale and ActivityCompat.shouldShowRequestPermissionRationale(fragment.activity, permission)
        return rationale
    }

    fun isPermissionsAllowed(activity: Context, vararg permissions: String): Boolean {
        var permissionAllowed = true

        for (permission in permissions) {
            permissionAllowed = permissionAllowed and (ActivityCompat.checkSelfPermission(activity,
                    permission) == PermissionChecker.PERMISSION_GRANTED)
        }
        return permissionAllowed
    }
}
