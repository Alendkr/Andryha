package com.example.weather.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.adapters.WeatherAdapter
import com.example.weather.adapters.WeatherModel
import com.example.weather.databinding.FragmentHoursBinding
import com.example.weather.databinding.ListItemBinding

class HoursFragment : Fragment() {
    private lateinit var adapter: WeatherAdapter
    private lateinit var binding: FragmentHoursBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
    }
    private fun initRcView() = with(binding){
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter()
        rcView.adapter = adapter
        val list = listOf(
            WeatherModel("",
                "12:00",
                "sunny",
                "-7°С",
                "",
                "",
                "",
                "")
        ,
        WeatherModel("",
                "13:00",
                "sunny",
                "-5°С",
                "",
                "",
                "",
                "")
        ,
        WeatherModel("",
                "14:00",
                "cloudy",
                "-8°С",
                "",
                "",
                "",
                "")
        )
        adapter.submitList(list)
    }
    companion object {

        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}