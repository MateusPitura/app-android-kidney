package com.example.my_kidney_app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddWater : Fragment() {
    private lateinit var addWater: Button
    private lateinit var db: AppDataBase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_water, container, false)

        db = AppDataBase.getDatabase(requireContext())
        val sp: SharedPreferences = requireContext().getSharedPreferences(
            Utils().sharedPreferencesKey,
            Context.MODE_PRIVATE
        )

        val cupSize = sp.getInt(Utils().cupSizeKey, 500)

        addWater = view.findViewById<Button>(R.id.add_water)

        addWater.text = "Adicionar $cupSize ml"

        addWater.setOnClickListener {
            val newEntity = Drink(milliliters = cupSize)
            lifecycleScope.launch {
                db.drinkDao().insert(newEntity)
            }
        }

        return view
    }
}