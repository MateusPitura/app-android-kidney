package com.example.my_kidney_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DrinkAdapter(
    private val context: Context,
    private val drinks: List<Drink>
) : ArrayAdapter<Drink>(context, 0, drinks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.today_drink_item, parent, false)

        val drink = drinks[position]

        val tvMilliliters = view.findViewById<TextView>(R.id.milliliters)
        val tvTime = view.findViewById<TextView>(R.id.time)

        tvMilliliters.text = "${drink.milliliters} ml"

        val time = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(drink.timestamp))
        tvTime.text = time

        return view
    }
}
