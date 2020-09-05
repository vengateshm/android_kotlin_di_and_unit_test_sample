package com.android.diandmocking.util

import android.content.SharedPreferences
import androidx.core.content.edit

private const val TIMESTAMP_PREF_KEY = "TIMESTAMP_PREF_KEY"

var SharedPreferences.timestamp: Long
    get() = getLong(TIMESTAMP_PREF_KEY, 0L)
    set(value) {
        edit(commit = true) {
            putLong(TIMESTAMP_PREF_KEY, value)
        }
    }