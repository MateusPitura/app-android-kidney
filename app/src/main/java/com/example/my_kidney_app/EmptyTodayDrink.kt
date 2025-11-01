package com.example.my_kidney_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmptyTodayDrink : Fragment() {
    private val waterTips = arrayOf(
        "Comece o dia com um copo de água para ativar seu metabolismo",
        "Após exercícios físicos, reponha os líquidos perdidos com bastante água",
        "Antes de dormir evite beber muita água para não interromper seu sono",
        "Reduza bebidas com cafeína e refrigerantes para manter a hidratação eficaz",
    )

    private lateinit var todayMessage: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_empty_today_drink, container, false)

        val randomIndex = (0 until waterTips.size).random()
        todayMessage = view.findViewById<TextView>(R.id.empty_message)
        todayMessage.text = waterTips[randomIndex]

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation({ temperature ->
                if (temperature !== null && temperature >= 30f) {
                    todayMessage.text =
                        "Hoje está um dia quente, com temperatura de $temperature°C! Beba mais água para manter-se hidratado"
                }
            })
        }

        return view
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(callback: (Double?) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    fetchTemperature(location.latitude, location.longitude, callback)
                } else {
                    requestLocationUpdate()
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdate() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun fetchTemperature(lat: Double, long: Double, callback: (Double?) -> Unit) {
        val call = RetrofitInstance.api.getWeather(lat, long)
        call.enqueue(object : Callback<Weather> {
            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    callback(body?.current_weather?.temperature)
                }
            }

            override fun onFailure(call: Call<Weather?>, t: Throwable) {}
        })
    }
}
