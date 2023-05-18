package com.example.weathernew

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.weathernew.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var viewFlipper: ViewFlipper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo?.isConnected == true

        val queryEditText = binding.root.findViewById<EditText>(R.id.query_edittext)
        val fullForecast = binding.root.findViewById<Button>(R.id.weekForecast_bt)
        val returnButton = binding.root.findViewById<ImageButton>(R.id.returnarrow_bt)
        returnButton.background = null
        queryEditText.background = null
        fullForecast.setBackgroundColor(Color.parseColor("#80FFFFFF"))

        viewFlipper = findViewById(R.id.viewFlipper)

        fullForecast.setOnClickListener {
            viewFlipper.showNext()
        }
        returnButton.setOnClickListener {
            viewFlipper.showPrevious()
        }


        getWeather("Сочи")


        queryEditText.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP && isConnected) {
                Toast.makeText(applicationContext, "Загрузка...", Toast.LENGTH_SHORT).show()
                getWeather(queryEditText.text.toString())
                return@OnKeyListener true
            }
            false
        })

        Toast.makeText(this, "Автор: Андреев В.", Toast.LENGTH_LONG).show()

        if (!isConnected) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show()
        }
    }

    private fun getWeather(query: String){
        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val url = "https://api.weatherapi.com/v1/forecast.json?key=ffc238ff6f9f42779e5163328232804&q=$query&days=3&aqi=no&alerts=no&lang=ru"

            val request = Request.Builder()
                .url(url)
                .build()

            //val displayWeatherTodayText = binding.root.findViewById<TextView>(R.id.forecastTodayMorning_tv)
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            val json = JSONObject(responseBody)
            val jsonObject = JSONObject(responseBody)
            try {


                val forecastObject = jsonObject.getJSONObject("forecast") //ут
                val forecastdayArray = forecastObject.getJSONArray("forecastday")


// 8:00
                val eightHourObject =
                    forecastdayArray.getJSONObject(0).getJSONArray("hour").getJSONObject(8)
                val eightHourTemp = eightHourObject.getDouble("temp_c").roundToInt()
                val eightHourConditionText =
                    eightHourObject.getJSONObject("condition").getString("text")
                val morningCode = eightHourObject.getJSONObject("condition").getString("code")




                val morningImage = binding.root.findViewById<ImageView>(R.id.MorningImage)
                val dayImage = binding.root.findViewById<ImageView>(R.id.DayImage)
                val eveningImage = binding.root.findViewById<ImageView>(R.id.EveningImage)
                val nightImage = binding.root.findViewById<ImageView>(R.id.NightImage)

                //туман 1030\
                //1087 гроза
                //1273 1276 гроза с дождем

                val rainWeatherCodes = listOf("1063","1072","1150","1180","1183","1186","1189","1192","1195","1273","1276","1207")
                val sunWeatherCodes = listOf("1000")
                val cloudWeatherCodes = listOf("1087","1273","1276")
                val thunderWeatherCodes = listOf("1003","1006","1009","1030","1135",)

//                if (rainWeatherCodes.contains(morningCode)){
//                    morningImage.setImageResource(R.drawable.rain)
//                    Log.d("MainActivity", "утро дождь")
//                }
                Log.d("MainActivity","утро: $morningCode")

                when (morningCode) {
                    in rainWeatherCodes -> {
                        morningImage.setImageResource(R.drawable.rain)
                        Log.d("MainActivity", "утро дождь")
                    }
                    in sunWeatherCodes -> {
                        morningImage.setImageResource(R.drawable.sunny)
                        Log.d("MainActivity", "утро солнце")
                    }
                    in cloudWeatherCodes -> {
                        morningImage.setImageResource(R.drawable.cloud)
                        Log.d("MainActivity", "утро облака")
                    }
                    in thunderWeatherCodes -> {
                        morningImage.setImageResource(R.drawable.thunder)
                        Log.d("MainActivity", "утро гроза")
                    }
                    else -> {
                        morningImage.setImageResource(R.drawable.cloud)
                        Log.d("MainActivity", "default")
                    }
                }




// 14:00
                val fourteenHourObject =
                    forecastdayArray.getJSONObject(0).getJSONArray("hour").getJSONObject(14)
                val fourteenHourTemp = fourteenHourObject.getDouble("temp_c").roundToInt()
                val fourteenHourConditionText =
                    fourteenHourObject.getJSONObject("condition").getString("text")
                val dayCode = eightHourObject.getJSONObject("condition").getString("code")
                //val dayImage = binding.root.findViewById<ImageView>(R.id.DayImage)
                Log.d("MainActivity","день: $dayCode")

                when (dayCode) {
                    in rainWeatherCodes -> {
                        dayImage.setImageResource(R.drawable.rain)
                        Log.d("MainActivity", "утро дождь")
                    }
                    in sunWeatherCodes -> {
                        dayImage.setImageResource(R.drawable.sunny)
                        Log.d("MainActivity", "утро солнце")
                    }
                    in cloudWeatherCodes -> {
                        dayImage.setImageResource(R.drawable.cloud)
                        Log.d("MainActivity", "утро облака")
                    }
                    in thunderWeatherCodes -> {
                        dayImage.setImageResource(R.drawable.thunder)
                        Log.d("MainActivity", "утро гроза")
                    }
                    else -> {
                        dayImage.setImageResource(R.drawable.cloud)
                        Log.d("MainActivity", "default")
                    }
                }


// 18:00
                val eighteenHourObject =
                    forecastdayArray.getJSONObject(0).getJSONArray("hour").getJSONObject(18)
                val eighteenHourTemp = eighteenHourObject.getDouble("temp_c").roundToInt()
                val eighteenHourConditionText =
                    eighteenHourObject.getJSONObject("condition").getString("text")
                val eveningCode = eightHourObject.getJSONObject("condition").getString("code")
                //val eveningImage = binding.root.findViewById<ImageView>(R.id.EveningImage)

                Log.d("MainActivity","вечер: $eveningCode")
                when (eveningCode) {
                    in rainWeatherCodes -> {
                        eveningImage.setImageResource(R.drawable.rain)
                        Log.d("MainActivity", "утро дождь")
                    }
                    in sunWeatherCodes -> {
                        eveningImage.setImageResource(R.drawable.sunny)
                        Log.d("MainActivity", "утро солнце")
                    }
                    in cloudWeatherCodes -> {
                        eveningImage.setImageResource(R.drawable.cloud)
                        Log.d("MainActivity", "утро облака")
                    }
                    in thunderWeatherCodes -> {
                        eveningImage.setImageResource(R.drawable.thunder)
                        Log.d("MainActivity", "утро гроза")
                    }
                    else -> {
                        eveningImage.setImageResource(R.drawable.cloud)
                        Log.d("MainActivity", "default")
                    }
                }
// 23:00
                val twentyThreeHourObject =
                    forecastdayArray.getJSONObject(0).getJSONArray("hour").getJSONObject(23)
                val twentyThreeHourTemp = twentyThreeHourObject.getDouble("temp_c").roundToInt()
                val twentyThreeHourConditionText =
                    twentyThreeHourObject.getJSONObject("condition").getString("text")
                val nightCode = eightHourObject.getJSONObject("condition").getString("code")
                //val nightImage = binding.root.findViewById<ImageView>(R.id.NightImage)

                Log.d("MainActivity","ночь: $nightCode")
                when (nightCode) {
                    in rainWeatherCodes -> {
                        nightImage.setImageResource(R.drawable.rain_night)
                        Log.d("MainActivity", "дождь")
                    }
                    in sunWeatherCodes -> {
                        nightImage.setImageResource(R.drawable.clear_night)
                        Log.d("MainActivity", "солнце")
                    }
                    in cloudWeatherCodes -> {
                        nightImage.setImageResource(R.drawable.cloud)
                        Log.d("MainActivity", "облака")
                    }
                    in thunderWeatherCodes -> {
                        nightImage.setImageResource(R.drawable.thunder)
                        Log.d("MainActivity", "гроза")
                    }
                    else -> {
                        nightImage.setImageResource(R.drawable.cloud)
                        Log.d("MainActivity", "default")
                    }
                }


                var morningText = binding.root.findViewById<TextView>(R.id.MorningCardText)
                var morningTemp = binding.root.findViewById<TextView>(R.id.MorningCardTemp)
                morningText.text = "Утро\n$eightHourConditionText"
                morningTemp.text = "$eightHourTemp°"

                var dayText = binding.root.findViewById<TextView>(R.id.DayCardText)
                var dayTemp = binding.root.findViewById<TextView>(R.id.DayCardTemp)
                dayText.text = "День\n$fourteenHourConditionText"
                dayTemp.text = "$fourteenHourTemp°"

                var eveningText = binding.root.findViewById<TextView>(R.id.EveningCardText)
                var eveningTemp = binding.root.findViewById<TextView>(R.id.EveningCardTemp)
                eveningText.text = "Вечер\n$eighteenHourConditionText"
                eveningTemp.text = "$eighteenHourTemp°"

                var nightText = binding.root.findViewById<TextView>(R.id.NightCardText)
                var nightTemp = binding.root.findViewById<TextView>(R.id.NightCardTemp)
                nightText.text = "Ночь\n$twentyThreeHourConditionText"
                nightTemp.text = "$twentyThreeHourTemp°"


                //displayWeatherTodayText.text = "Утром $eightHourTemp, $eightHourConditionText\nДнем: $fourteenHourTemp, $fourteenHourConditionText\nВечером: $eighteenHourTemp, $eighteenHourConditionText\nНочью: $twentyThreeHourTemp, $twentyThreeHourConditionText";

                val location_name = json.getJSONObject("location").getString("name")
                val current_temp_c = json.getJSONObject("current").getDouble("temp_c").roundToInt()
                var current_condition_text =
                    json.getJSONObject("current").getJSONObject("condition").getString("text")

                val message = "$current_temp_c градусов, $current_condition_text\nпогода на "
                Log.d("MainActivity", "$message")

                withContext(Dispatchers.Main) {
                    val temp_tw = binding.root.findViewById<TextView>(R.id.temp_textview)
                    temp_tw.text = "$current_temp_c"
                    val forecast_tw = binding.root.findViewById<TextView>(R.id.forecast_textview)
                    forecast_tw.text = "$current_condition_text"
                }
                val queryEditText = binding.root.findViewById<EditText>(R.id.query_edittext)
                queryEditText.setText("$location_name")
                val fullForecastCity = binding.root.findViewById<TextView>(R.id.FullForecastCity)
                fullForecastCity.text = location_name
            }
            catch (e: JSONException){
                runOnUiThread {
                    Log.e("MainActivity", "Ошибка: ${e.message}")
                    Toast.makeText(applicationContext, "Город по вашему запросу не найден!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
