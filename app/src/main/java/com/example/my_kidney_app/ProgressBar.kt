package com.example.my_kidney_app

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Animatable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProgressBar : Fragment() {
    private lateinit var db: AppDataBase
    private lateinit var amountBar: View
    private lateinit var amountPercent: TextView
    private lateinit var amountTotal: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress_bar, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        view.post { // Necessário para poder acessar o ImageView do kidney
            db = AppDataBase.getDatabase(requireContext())
            val sp: SharedPreferences = requireContext().getSharedPreferences(
                Utils().sharedPreferencesKey,
                Context.MODE_PRIVATE
            )

            val todayGoal = sp.getInt(Utils().todayGoalKey, 2000)

            amountBar = view.findViewById<View>(R.id.amount_bar)
            amountPercent = view.findViewById<TextView>(R.id.amount_percent)
            amountTotal = view.findViewById<TextView>(R.id.amount_total)

            val kidney = (requireActivity() as? MainActivity)?.getKidneyView()

            if (kidney == null) {
                Utils().log(requireActivity().toString())
            }

            (kidney?.drawable as? Animatable)?.start() // Inicia a animação dos olhos

            val amountBarParams = amountBar.layoutParams as ConstraintLayout.LayoutParams

            lifecycleScope.launch {
                db.drinkDao().getTodayAmount().collectLatest { amount ->
                    var percent = 0f
                    if (amount !== null) {
                        percent =
                            if (amount >= todayGoal) 1f else amount / todayGoal.toFloat() // toFloat evita truncamento
                    }

                    amountBarParams.matchConstraintPercentWidth = percent
                    val percentParsed = (percent * 100).toInt()
                    if (percentParsed <= 1) {
                        kidney?.setImageResource(R.drawable.kidney_very_bad)
                    } else if (percentParsed <= 25) {
                        kidney?.setImageResource(R.drawable.kidney_bad)
                    } else if (percentParsed <= 50) {
                        kidney?.setImageResource(R.drawable.kidney_default)
                    } else if (percentParsed <= 75) {
                        kidney?.setImageResource(R.drawable.kidney_good)
                    } else {
                        kidney?.setImageResource(R.drawable.kidney_very_good)
                    }
                    amountPercent.text = "$percentParsed%"
                    amountTotal.text = "${amount ?: 0} / $todayGoal ml"

                    amountBar.layoutParams = amountBarParams
                }
            }
        }
    }
}
