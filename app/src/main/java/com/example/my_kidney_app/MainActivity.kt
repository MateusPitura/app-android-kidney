package com.example.my_kidney_app

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDataBase
    private lateinit var readWater: Button
    private lateinit var todayDrink: ListView

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
        val addWater = findViewById<Button>(R.id.add_water)
        val todayDrink = findViewById<ListView>(R.id.today_drinks)

        (img.drawable as? Animatable)?.start()

        addWater.setOnClickListener {
            val newEntity = Drink(milliliters = 400)
            lifecycleScope.launch {
                db.drinkDao().insert(newEntity)
            }
        }

        lifecycleScope.launch {
            db.drinkDao().getAll().collectLatest { items ->
                val adapter = DrinkAdapter(
                    this@MainActivity,
                    items
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