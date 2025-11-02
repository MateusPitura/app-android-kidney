package com.example.my_kidney_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

class NavFab : Fragment() {
    private lateinit var button: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nav_fab, container)

        button = view.findViewById<ImageButton>(R.id.navFab)

        val currentActivity = requireActivity()

        when (currentActivity) {
            is SettingActivity -> {
                button.setImageResource(R.drawable.home)
            }

            is MainActivity -> {
                button.setImageResource(R.drawable.settings)
            }
        }

        button.setOnClickListener {
            val currentActivity = requireActivity()

            var intent: Intent?
            when (currentActivity) {
                is SettingActivity -> {
                    intent = Intent(currentActivity, MainActivity::class.java)
                }

                is MainActivity -> {
                    intent = Intent(currentActivity, SettingActivity::class.java)
                }

                else -> {
                    intent = null
                }
            }
            startActivity(intent)
        }

        return view
    }
}