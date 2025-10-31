package com.example.my_kidney_app

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDataBase
    private lateinit var todayDrink: ListView
    private lateinit var addWater: Button

    private lateinit var waterAmount1000: TextView
    private lateinit var waterAmount100: TextView
    private lateinit var waterAmount10: TextView
    private lateinit var waterAmount1: TextView

    private lateinit var hourValue10: TextView
    private lateinit var hourValue1: TextView
    private lateinit var minuteValue10: TextView
    private lateinit var minuteValue1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDataBase.getDatabase(this)

        val img = findViewById<ImageView>(R.id.kidney)
        addWater = findViewById<Button>(R.id.add_water)
        todayDrink = findViewById<ListView>(R.id.today_drinks)

        (img.drawable as? Animatable)?.start()

        addWater.setOnClickListener {
            val newEntity = Drink(milliliters = 400)
            lifecycleScope.launch {
                db.drinkDao().insert(newEntity)
            }
        }

        val bottomSheet = BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
        bottomSheet.setContentView(view)
        view.findViewById<Button>(R.id.closeButton).setOnClickListener {
            bottomSheet.dismiss()
        }

        lifecycleScope.launch {
            db.drinkDao().getAll().collectLatest { items ->
                val adapter = DrinkAdapter(
                    this@MainActivity,
                    items,
                    { drink ->
                        waterAmount1000 = view.findViewById<TextView>(R.id.amount_value_1000)
                        waterAmount100 = view.findViewById<TextView>(R.id.amount_value_100)
                        waterAmount10 = view.findViewById<TextView>(R.id.amount_value_10)
                        waterAmount1 = view.findViewById<TextView>(R.id.amount_value_1)

                        val str = drink.milliliters.toString().padStart(4, '0')
                        waterAmount1000.text = str[0].toString()
                        waterAmount100.text = str[1].toString()
                        waterAmount10.text = str[2].toString()
                        waterAmount1.text = str[3].toString()

                        hourValue10 = view.findViewById<TextView>(R.id.hour_value_10)
                        hourValue1 = view.findViewById<TextView>(R.id.hour_value_1)
                        minuteValue10 = view.findViewById<TextView>(R.id.minute_value_10)
                        minuteValue1 = view.findViewById<TextView>(R.id.minute_value_1)

                        val timeParsed = SimpleDateFormat("HHmm", Locale.getDefault()).format(Date(drink.timestamp))
                        hourValue10.text = timeParsed[0].toString()
                        hourValue1.text = timeParsed[1].toString()
                        minuteValue10.text = timeParsed[2].toString()
                        minuteValue1.text = timeParsed[3].toString()

                        bottomSheet.show()
                    }
                )
                todayDrink.adapter = adapter
                setListViewHeightBasedOnItems(todayDrink)
            }
        }
    }

    fun setListViewHeightBasedOnItems(listView: ListView) {
        val listAdapter = listView.adapter ?: return

        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.UNSPECIFIED
            )
            totalHeight += listItem.measuredHeight
        }

        totalHeight += listView.dividerHeight * (listAdapter.count - 1)

        val params = listView.layoutParams
        params.height = totalHeight
        listView.layoutParams = params
        listView.requestLayout()
    }
}