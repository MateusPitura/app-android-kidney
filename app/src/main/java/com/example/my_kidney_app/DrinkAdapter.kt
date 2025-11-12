package com.example.my_kidney_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

// Permite customizar cada item do ListView
class DrinkAdapter(
    private val context: Context,
    private val drinks: List<Drink>,
    private val onItemClick: (Drink) -> Unit
) : ArrayAdapter<Drink>(context, 0, drinks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.today_drink_item, parent, false)

        val drink = drinks[position]

        val milliliters = view.findViewById<TextView>(R.id.milliliters)
        val time = view.findViewById<TextView>(R.id.time)

        milliliters.text = "${drink.milliliters} ml"

        val timeParsed = Utils().formatDate("HH:mm", drink.timestamp)
        time.text = timeParsed

        view.setOnClickListener {
            onItemClick(drink)
        }

        return view
    }
}
