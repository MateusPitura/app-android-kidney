package com.example.my_kidney_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var goBackButton: ImageButton
    private lateinit var cupSizeInput: EditText
    private lateinit var todayGoalInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        goBackButton = findViewById<ImageButton>(R.id.go_back)
        cupSizeInput = findViewById<EditText>(R.id.cup_size)
        todayGoalInput = findViewById<EditText>(R.id.today_goal)

        val sp: SharedPreferences =
            getSharedPreferences(Utils().sharedPreferencesKey, Context.MODE_PRIVATE)

        val defaultCupSize = sp.getInt(Utils().cupSizeKey, 500)
        val defaultTodayGoal = sp.getInt(Utils().todayGoalKey, 2000)

        cupSizeInput.hint = defaultCupSize.toString()
        todayGoalInput.hint = defaultTodayGoal.toString()

        val edit = sp.edit()

        goBackButton.setOnClickListener {
            val cupSize = cupSizeInput.text.toString().toIntOrNull()
            val todayGoal = todayGoalInput.text.toString().toIntOrNull()

            if (cupSize != null && cupSize > 0 && cupSize < 10000 && todayGoal != null && todayGoal > 0 && todayGoal < 10000) {
                edit.putInt(Utils().cupSizeKey, cupSize)
                edit.putInt(Utils().todayGoalKey, todayGoal)
                edit.apply()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}