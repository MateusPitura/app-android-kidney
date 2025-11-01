package com.example.my_kidney_app

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDataBase
    private lateinit var todayDrink: ListView
    private lateinit var addWater: Button
    private lateinit var deleteDrink: Button
    private lateinit var rootView: ScrollView

    private val amountValues = arrayOf(
        R.id.amount_value_1000,
        R.id.amount_value_100,
        R.id.amount_value_10,
        R.id.amount_value_1
    )
    private val amountButtons = arrayOf(
        R.id.amount_up_1000,
        R.id.amount_up_100,
        R.id.amount_up_10,
        R.id.amount_up_1,
        R.id.amount_down_1000,
        R.id.amount_down_100,
        R.id.amount_down_10,
        R.id.amount_down_1
    )
    private val timeValues = arrayOf(
        R.id.minute_value_10,
        R.id.minute_value_1
    )
    private val timeButtons = arrayOf(
        R.id.minute_up_10,
        R.id.minute_up_1,
        R.id.minute_down_10,
        R.id.minute_down_1
    )

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

        rootView = findViewById<ScrollView>(R.id.main)

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
            db.drinkDao().getTodayDrinks().collectLatest { items ->
                val adapter = DrinkAdapter(
                    this@MainActivity,
                    items,
                    { drink ->
                        val millilitersParsed = drink.milliliters.toString().padStart(4, '0')
                        for (i in amountValues.indices) {
                            val upButton = view.findViewById<ImageButton>(amountButtons[i])
                            val downButton = view.findViewById<ImageButton>(amountButtons[i + 4])
                            val value = view.findViewById<TextView>(amountValues[i])
                            value.text = millilitersParsed[i].toString()
                            handleValue(upButton, value, 1, { v -> v < 9 })
                            handleValue(downButton, value, -1, { v -> v > 0 })
                        }

                        val timeParsed = SimpleDateFormat(
                            "HHmm",
                            Locale.getDefault()
                        ).format(Date(drink.timestamp))
                        for (i in timeValues.indices) {
                            val upButton = view.findViewById<ImageButton>(timeButtons[i])
                            val downButton = view.findViewById<ImageButton>(timeButtons[i + 2])
                            val value = view.findViewById<TextView>(timeValues[i])
                            value.text = timeParsed[i + 2].toString()
                            handleValue(upButton, value, 1, { v -> v < (if (i == 0) 5 else 9) })
                            handleValue(downButton, value, -1, { v -> v > 0 })
                        }

                        val hourValue10 = view.findViewById<TextView>(R.id.hour_value_10)
                        val hourValue1 = view.findViewById<TextView>(R.id.hour_value_1)
                        val hourUp = view.findViewById<ImageButton>(R.id.hour_up)
                        val hourDown = view.findViewById<ImageButton>(R.id.hour_down)
                        hourValue10.text = timeParsed[0].toString()
                        hourValue1.text = timeParsed[1].toString()
                        handleTime(hourUp, hourValue10, hourValue1, 1, { v -> v < 23 })
                        handleTime(hourDown, hourValue10, hourValue1, -1, { v -> v > 0 })

                        deleteDrink = view.findViewById<Button>(R.id.deleteDrink)
                        deleteDrink.setOnClickListener {
                            lifecycleScope.launch {
                                db.drinkDao().deleteById(drink.id)
                                Snackbar.make(rootView, "Registro removido", Snackbar.LENGTH_LONG)
                                    .setAction("Desfazer") {
                                        val newEntity = Drink(milliliters = drink.milliliters, timestamp = drink.timestamp)
                                        lifecycleScope.launch {
                                            db.drinkDao().insert(newEntity)
                                        }
                                    }
                                    .setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.yellow))
                                    .show()
                                bottomSheet.dismiss()
                            }
                        }

                        bottomSheet.show()
                    }
                )
                todayDrink.adapter = adapter
                setListViewHeightBasedOnItems(todayDrink)
            }
        }
    }

    fun handleValue(
        button: ImageButton,
        textView: TextView,
        increment: Int,
        expression: (Int) -> Boolean
    ) {
        button.setOnClickListener {
            val value = textView.text.toString().toInt()
            if (expression(value)) textView.text = (value + increment).toString()
        }
    }

    fun handleTime(
        button: ImageButton,
        decimal: TextView,
        unit: TextView,
        increment: Int,
        expression: (Int) -> Boolean
    ) {
        button.setOnClickListener {
            val value10 = decimal.text.toString().toInt() * 10
            val value1 = unit.text.toString().toInt()
            val sum = value10 + value1
            if (expression(sum)) {
                val value = (sum + increment).toString().padStart(2, '0')
                decimal.text = value[0].toString()
                unit.text = value[1].toString()
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