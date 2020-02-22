package com.example.pickle.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi

class PermissionUtils {
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun neverAskAgainSelected(activityCompat: Activity, permission: String): Boolean {
        val prevShouldShowStatus: Boolean = getRatinaleDisplayStatus(activityCompat, permission)
        val currShouldShowStatus: Boolean =
            activityCompat.shouldShowRequestPermissionRationale(permission)
        return prevShouldShowStatus != currShouldShowStatus;
    }

    fun setShouldShowStatus(context: Context, permission: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("permissions", 0)
        sharedPreferences.edit().putBoolean(permission, true).apply()
    }

    fun getRatinaleDisplayStatus(context: Context, permission: String): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("permissions", 0)
        return sharedPreferences.getBoolean(permission, false)
    }
}
