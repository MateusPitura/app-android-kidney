package com.example.my_kidney_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class EmptyTodayDrink : Fragment() {
    private val waterTips = arrayOf(
        "Comece o dia com um copo de água para ativar seu metabolismo",
        "Após exercícios físicos, reponha os líquidos perdidos com bastante água",
        "Antes de dormir evite beber muita água para não interromper seu sono",
        "Reduza bebidas com cafeína e refrigerantes para manter a hidratação eficaz",
    )

    private lateinit var todayMessage: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_empty_today_drink, container, false)

        val randomIndex = (0 until waterTips.size).random()
        todayMessage = view.findViewById<TextView>(R.id.empty_message)
        todayMessage.text = waterTips[randomIndex]

        return view
    }
}
