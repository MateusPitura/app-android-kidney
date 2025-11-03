package com.example.my_kidney_app

import android.util.Log

class Utils {
    public val sharedPreferencesKey = "KIDNEY_PREFERENCES"
    public val todayGoalKey = "TODAY_GOAL_KEY"
    public val cupSizeKey = "CUP_SIZE_KEY"

    fun log(content: Any) {
        Log.i("LOG", content.toString())
    }
}