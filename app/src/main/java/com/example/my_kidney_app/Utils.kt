package com.example.my_kidney_app

import android.util.Log

class Utils {
    public val sharedPreferencesKey = "KIDNEY_PREFERENCES"

    fun log(content: Any) {
        Log.i("LOG", content.toString())
    }
}