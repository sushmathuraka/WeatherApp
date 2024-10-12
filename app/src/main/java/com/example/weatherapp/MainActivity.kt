package com.example.weatherapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.service.notification.Condition
import androidx.appcompat.widget.SearchView
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import  java.util.Locale
import android. content. Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            setContentView(binding.root)
           

            fetchWeatherData("jaipur")
            SearchCity()

        }, 1000)
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

private  fun fetchWeatherData(cityName: String){
    val retrofit= Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .build().create(ApiInterface::class.java)

    val response = retrofit.getWeatherData(cityName, "c260280449147bbbb11fd77703837062", "metric")
    response.enqueue(object : Callback<WeatherApp>{
        override fun onResponse(
            call: Call<WeatherApp?>, response: Response<WeatherApp> )
        {
            val responseBody=response.body()
            if(response.isSuccessful && responseBody !=null){
                val temperature=responseBody.main.temp.toString()
                val humidity=responseBody.main.humidity
                val windSpeed=responseBody.wind.speed
                val sunRise=responseBody.sys.sunrise.toLong()
               val sunSet=responseBody.sys.sunset.toLong()
               val seaLevel=responseBody.main.pressure
                val condition=responseBody.weather.firstOrNull()?.main?:"unknow"
                var maxTemp=responseBody.main.temp_max
                var minTemp=responseBody.main.temp_min
                binding.temp.text="$temperature °C"
                binding.weather.text=condition
                binding.maxTemp.text="Max Temp:$maxTemp °C"
                binding.minTemp.text="Min Temp:$minTemp °C"
                binding.humidity.text="$humidity %"
                binding.windSpeed.text="$windSpeed m/s %"
                binding.sunRise.text="${time(sunRise)} "
                binding.sunSet.text="${time(sunSet)} "
                binding.sea.text="$seaLevel hpa %"
                binding.condition.text=condition
                binding.day.text = dayName(System.currentTimeMillis())
                binding.date.text = date()
                    binding.cityName.text="$cityName"
                //Log.d(TAG, "onResponse: $temperature")
                changeImgsAccordingToWeatherCondition(condition)

            }
        }



        override fun onFailure(
            call: Call<WeatherApp?>, t: Throwable )
        {
            TODO("Not yet implemented")
        }

    })

}
    private fun changeImgsAccordingToWeatherCondition(conditions: String){
        when(conditions){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Rain","Little Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light snow","snow","scattered clouds","Moderate Snow","Heavy Snow","Blizzard","rain and snow","sleet"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }
    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm ", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
}
