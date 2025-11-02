package com.example.my_kidney_app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

const val SILENT_MODE_KEY = "SILENT_MODE_KEY"

class SilentButton : Fragment() {
    private lateinit var notifications: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_silent_button, container, false)

        notifications = view.findViewById<ImageButton>(R.id.notifications)
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences(
                Utils().sharedPreferencesKey,
                Context.MODE_PRIVATE
            )
        var isSilentMode = sharedPreferences.getBoolean(SILENT_MODE_KEY, false)
        toggleButton(isSilentMode)
        val sharedPreferencesEditor = sharedPreferences.edit()

        notifications.setOnClickListener {
            isSilentMode = sharedPreferences.getBoolean(SILENT_MODE_KEY, false)
            sharedPreferencesEditor.putBoolean(SILENT_MODE_KEY, !isSilentMode)
            sharedPreferencesEditor.apply()
            toggleButton(!isSilentMode)
        }

        return view;
    }

    fun toggleButton(isSilentMode: Boolean) {
        if (isSilentMode) {
            vibratePhone(requireContext())
            notifications.setImageResource(R.drawable.notifications_off)
            notifications.setBackgroundResource(R.drawable.solid_shadow_red)
        } else {
            notifications.setImageResource(R.drawable.notifications)
            notifications.setBackgroundResource(R.drawable.solid_shadow_pink)
        }
    }

    fun vibratePhone(context: Context, duration: Long = 200) {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}