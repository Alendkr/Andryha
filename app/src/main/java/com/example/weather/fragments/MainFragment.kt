package com.example.weather.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weather.MainViewModel
import com.example.weather.adapters.VpAdapter
import com.example.weather.adapters.WeatherModel
import com.example.weather.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "c58d557b34a24d18a3d180939242809"

class MainFragment : Fragment() {
    private val flist = listOf(
        HoursFragment.newInstance(), DaysFragment.newInstance()
    )

    private  val tlist = listOf(
        "Hours",
        "Days"
    )
    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        updateCurrentCard()
        requestWeatherData("Kostroma")
    }

    private fun init() = with(binding){
        val adapter = VpAdapter(activity as FragmentActivity, flist)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp){
            tab, pos -> tab.text = tlist[pos]
        }.attach()
    }

    private fun updateCurrentCard() = with(binding){
        model.liveDataCurrent.observe(viewLifecycleOwner){
            val maxMinTemp = "${it.maxTemp}°/${it.minTemp}°C"
            tvData.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp
            tvCondition.text = it.condition
            tvMaxMin.text = maxMinTemp
            Picasso.get().load("https:" + it.imageUrl).into(imWeather)


        }
    }

    private fun permissionListener(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission(){
        if(!isPermissionsGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun parseWeatherData(result: String){
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel){

        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"), //city
            mainObject.getJSONObject("current").getString("last_updated"), //time
            mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("text"), //condition
            mainObject.getJSONObject("current").getString("temp_c"), //currentTemp
            weatherItem.maxTemp, //maxTemp
            weatherItem.minTemp, //minTemp
            mainObject.getJSONObject("current").getJSONObject("condition")
                .getString("icon"), //imageUrl
            weatherItem.hours //hours

        )
        model.liveDataCurrent.value = item
        mainObject.getString("location")

    }

    private fun parseDays(mainObject: JSONObject):List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name") //city
        for (i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name, //city
                day.getString("date"), //time
                day.getJSONObject("day")
                    .getJSONObject("condition").getString("text"), //condition
                "",
                day.getJSONObject("day").getString("maxtemp_c"), //maxTemp
                day.getJSONObject("day").getString("mintemp_c"), //minTemp
                day.getJSONObject("day")
                    .getJSONObject("condition").getString("icon"), //imageUrl
                day.getJSONArray("hour").toString(), //hours

            )
            list.add(item)
        }
        return list
    }

    private fun requestWeatherData(city:String){
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "5" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            {
                result -> parseWeatherData(result)
            },
            {
                error -> Log.d("MyLog", "Error:$error")
            }
        )
        queue.add(request)
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()

    }
            }

