package com.actualplatform.android.activation.development.ui

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable

public inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> {
        getParcelableExtra(key, T::class.java)
    }

    else -> {
        @Suppress("DEPRECATION")
        getParcelableExtra(key)
    }
}

public inline fun <reified T : Parcelable> Intent.parcelableArray(key: String): ArrayList<T>? =
    when {
        SDK_INT >= 33 -> {
            getParcelableArrayListExtra(key, T::class.java)
        }

        else -> {
            @Suppress("DEPRECATION")
            getParcelableArrayListExtra(key)
        }
    }
