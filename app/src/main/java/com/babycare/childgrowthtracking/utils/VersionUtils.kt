package com.babycare.childgrowthtracking.utils

import android.os.Build

object VersionUtils {

    fun hasLollipop(): Boolean {
        return Build.VERSION.SDK_INT >= 21
    }

    fun hasMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= 23
    }

    fun hasNougat(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    fun hasNougatMR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    }

    fun hasOreo(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    fun hasP(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    fun hasQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }


    fun currentVersion(): Int = Build.VERSION.SDK_INT
}
