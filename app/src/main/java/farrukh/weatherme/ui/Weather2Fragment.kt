package farrukh.weatherme.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import farrukh.weatherme.adapter.DayAdapter
import farrukh.weatherme.adapter.HourAdapter
import farrukh.weatherme.data_class.Day
import farrukh.weatherme.data_class.Hour
import farrukh.weatherme.databinding.FragmentWeather2Binding
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Weather2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Weather2Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding:FragmentWeather2Binding
    lateinit var hours: MutableList<Hour>
    lateinit var days: MutableList<Day>
    var position_r = 0
    var url = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeather2Binding.inflate(inflater, container, false)
        var location = "Tashkent"
        url =
            "http://api.weatherapi.com/v1/forecast.json?key=9bcfb053b7d247fda8c53154230810&q=Tashkent&days=5&aqi=yes&alerts=yes"

        val requestque = Volley.newRequestQueue(requireContext())
        hours = mutableListOf()
        days = mutableListOf()

        var currentTime: String? = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        var currentTime_day: String? = LocalDate.now().format(DateTimeFormatter.ofPattern("dd"))
        var currentTime_hour: String? = LocalTime.now().toString()
        var time_min_local = currentTime_hour?.substring(0, 2)
        Log.d("TAG", "onCreateView: $currentTime_day")



        binding.addLocation.setOnClickListener {
            binding.addLocation.visibility = View.GONE
            binding.location.visibility = View.INVISIBLE
            binding.searchText.visibility = View.VISIBLE
            binding.searchText.findFocus()
            binding.send.isEnabled = true

            binding.send.visibility = View.VISIBLE
        }








        var request = JsonObjectRequest(url, object : Response.Listener<JSONObject> {
            override fun onResponse(response: JSONObject?) {

                var forecast = response?.getJSONObject("forecast")
                var location = response?.getJSONObject("location")
                var current = response?.getJSONObject("current")

                var condition = current?.getJSONObject("condition")

                var forecastday = forecast?.getJSONArray("forecastday")


                var state = condition?.getString("icon")
                var conditio = condition?.getString("text")

                var name = location?.getString("name")

                var temp_c = current?.getString("temp_c")
                var date = current?.getString("last_updated")
                var wind_kph = current?.getString("wind_kph")
                var wind_dir = current?.getString("wind_dir")
                var hum = current?.getString("humidity")



                binding.location.setText(name)
                binding.date.setText("Today , " + date)
                binding.temprature.setText(temp_c!!.substring(0, 2) + " °C")

                binding.state.load("http:" + state)
                binding.wind.setText(wind_kph + " kph ," + wind_dir)
                binding.humidity.setText(hum + "%")


                var resobj = forecastday!!.getJSONObject(position_r)

                val day = resobj.getJSONObject("day")

                val astro = resobj.getJSONObject("astro")

                val sunrise = astro.getString("sunrise")
                val sunset = astro.getString("sunset")

                val uv = current?.getString("uv")
//                    val max_wind = day.getString("maxwind_kph")
                val pressure = current?.getString("pressure_mb")
                val vision = current?.getString("vis_km")
                val chance_of_rain = day.getString("daily_chance_of_rain")
                val chance_of_snow = day.getString("daily_chance_of_snow")

                val maxtemp_c = day.getString("maxtemp_c")
                val mintemp_c = day.getString("mintemp_c")



                binding.textView4.setText(
                    conditio + "  " + maxtemp_c!!.substring(
                        0,
                        2
                    ) + "°/" + mintemp_c!!.substring(0, 2) + "°"
                )
                binding.sunset.text = "Sunset " + sunset
                binding.sunrise.text = "Sunrise " + sunrise
                binding.avgVision.text = "Vision in km " + vision
                binding.chanceOfSnow.text = "Chance of snow " + chance_of_snow + " %"
                binding.chanceOfRain.text = "Chance of rain " + chance_of_rain + " %"
                binding.pressure.text = "Pressure " + pressure + " mba"
                binding.uv.text = "Uv " + uv


                for (i in 0 until forecastday.length()) {
                    var resobJ = forecastday!!.getJSONObject(i)
                    val day = resobJ!!.getJSONObject("day")
                    val text = resobJ.getString("date")
                    val condition = day.getJSONObject("condition")

                    val state = condition.getString("text")
                    val img = condition.getString("icon")

                    val max_temp = day.getString("maxtemp_c")
                    val min_temp = day.getString("mintemp_c")
                    val date = text.substring(text.length - 5, text.length)

                    val day_object =
                        Day(date, state, max_temp.substring(0, 2), min_temp.substring(0, 2), img)
                    days.add(day_object)
                    var manager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                    binding.dayRec.layoutManager = manager
                    binding.dayRec.adapter = DayAdapter(days, object : DayAdapter.onItemClick {
                        override fun onDayClick(position: Int) {
                            resobj = forecastday!!.getJSONObject(position_r)

                            var date_day = resobj.getString("date")
                            position_r = position
                            var hour = resobj.getJSONArray("hour")
                            hours.clear()
                            for (i in 0 until hour.length()) {
                                val hour_obj = hour.getJSONObject(i)
                                val time_date = hour_obj.getString("time")
                                val time =
                                    time_date.substring(time_date.length - 5, time_date.length)
                                val time_now = time_date.substring(0, 10)
                                val temp_hour = hour_obj.getString("temp_c")
                                val wind_hour = hour_obj.getString("wind_mph")
                                val condition_hour = hour_obj.getJSONObject("condition")
                                val img_hour = condition_hour.getString("icon")
                                val hour =
                                    Hour(time, temp_hour.substring(0, 2), wind_hour, img_hour)
                                Log.d("TAG", "onDayClick: $time_now + $date_day")
                                if (time_now == date_day) {
                                    hours.add(hour)

                                    var manager =
                                        LinearLayoutManager(
                                            requireContext(),
                                            LinearLayoutManager.HORIZONTAL,
                                            false
                                        )

                                    binding.dailyHourRv.layoutManager = manager
                                    binding.dailyHourRv.adapter = HourAdapter(hours)

                                }

                            }
                        }

                    })


                }

                resobj = forecastday!!.getJSONObject(position_r)

                var date_day = resobj.getString("date")
                Log.d("TAG", "onResponse: $date_day")
                var hour = resobj.getJSONArray("hour")
                if (date_day == currentTime) {
                    for (i in 0 until hour.length()) {
                        val hour_obj = hour.getJSONObject(i)
                        val time_date = hour_obj.getString("time")
                        val time = time_date.substring(time_date.length - 5, time_date.length)
                        val time_now = time.substring(0, 2)
                        val temp_hour = hour_obj.getString("temp_c")
                        val wind_hour = hour_obj.getString("wind_mph")
                        val condition_hour = hour_obj.getJSONObject("condition")
                        val img_hour = condition_hour.getString("icon")
                        val hour = Hour(time, temp_hour.substring(0, 2), wind_hour, img_hour)

                        if (time_min_local!!.toInt() < time_now.toInt()) {
                            hours.add(hour)

                            var manager =
                                LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )

                            binding.dailyHourRv.layoutManager = manager
                            binding.dailyHourRv.adapter = HourAdapter(hours)
                        }


                    }
                }


//                    Log.d("TAG", "onResponse: $hour")
            }


        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                Log.d("TAG", "onErrorResponse: $error")
            }

        })

        binding.send.setOnClickListener {
            hours.clear()
            days.clear()

            if (binding.searchText.text.length > 0 && binding.searchText.text.isNotEmpty()) {
                location = binding.searchText.text.toString()
                binding.addLocation.visibility = View.VISIBLE
                binding.searchText.visibility = View.GONE
                binding.location.visibility = View.VISIBLE
                binding.send.visibility = View.GONE


                url =
                    "http://api.weatherapi.com/v1/forecast.json?key=9bcfb053b7d247fda8c53154230810&q=" + location + "&days=5&aqi=yes&alerts=yes"
                Log.d("TAG", "onCreateView: $url")



                request = JsonObjectRequest(url, object : Response.Listener<JSONObject> {
                    override fun onResponse(response: JSONObject?) {

                        var forecast = response?.getJSONObject("forecast")
                        var location = response?.getJSONObject("location")
                        var current = response?.getJSONObject("current")

                        var condition = current?.getJSONObject("condition")

                        var forecastday = forecast?.getJSONArray("forecastday")


                        var state = condition?.getString("icon")
                        var conditio = condition?.getString("text")

                        var name = location?.getString("name")

                        var temp_c = current?.getString("temp_c")
                        var date = current?.getString("last_updated")
                        var wind_kph = current?.getString("wind_kph")
                        var wind_dir = current?.getString("wind_dir")
                        var hum = current?.getString("humidity")



                        binding.location.setText(name)
                        binding.date.setText("Today , "+date)
                        binding.temprature.setText(temp_c+" °C")

                        binding.state.load("http:"+state)
                        binding.wind.setText(wind_kph+" kph ,"+wind_dir)
                        binding.humidity.setText(hum+"%")


                        var resobj = forecastday!!.getJSONObject(0)

                        val day = resobj.getJSONObject("day")

                        val astro = resobj.getJSONObject("astro")

                        val sunrise = astro.getString("sunrise")
                        val sunset = astro.getString("sunset")

                        val uv = current?.getString("uv")
//                    val max_wind = day.getString("maxwind_kph")
                        val pressure = current?.getString("pressure_mb")
                        val vision = current?.getString("vis_km")
                        val chance_of_rain = day.getString("daily_chance_of_rain")
                        val chance_of_snow = day.getString("daily_chance_of_snow")

                        val maxtemp_c = day.getString("maxtemp_c")
                        val mintemp_c = day.getString("mintemp_c")

                        val date_day = resobj.getString("date")

                        binding.textView4.setText(conditio + "  " + maxtemp_c + "°/" + mintemp_c + "°")
                        binding.sunset.text = "Sunset " + sunset
                        binding.sunrise.text = "Sunrise " + sunrise
                        binding.avgVision.text = "Vision in km " + vision
                        binding.chanceOfSnow.text = "Chance of snow " + chance_of_snow + " %"
                        binding.chanceOfRain.text = "Chance of rain " + chance_of_rain + " %"
                        binding.pressure.text = "Pressure " + pressure + " mba"
                        binding.uv.text = "Uv " + uv


                        for (i in 0 until forecastday.length()) {
                            var resobJ = forecastday!!.getJSONObject(i)
                            val day = resobJ!!.getJSONObject("day")
                            val text = resobJ.getString("date")
                            val condition = day.getJSONObject("condition")

                            val state = condition.getString("text")
                            val img = condition.getString("icon")

                            val max_temp = day.getString("maxtemp_c")
                            val min_temp = day.getString("mintemp_c")
                            val date = text.substring(text.length - 5, text.length)

                            val day_object = Day(date, state, max_temp, min_temp, img)
                            days.add(day_object)
                            var manager =
                                LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )

                            binding.dayRec.layoutManager = manager
                            binding.dayRec.adapter =
                                DayAdapter(days, object : DayAdapter.onItemClick {
                                    override fun onDayClick(position: Int) {
                                        resobj = forecastday!!.getJSONObject(position_r)

                                        var date_day = resobj.getString("date")
                                        position_r = position
                                        var hour = resobj.getJSONArray("hour")
                                        hours.clear()
                                        for (i in 0 until hour.length()) {
                                            val hour_obj = hour.getJSONObject(i)
                                            val time_date = hour_obj.getString("time")
                                            val time =
                                                time_date.substring(
                                                    time_date.length - 5,
                                                    time_date.length
                                                )
                                            val time_now = time_date.substring(0, 10)
                                            val temp_hour = hour_obj.getString("temp_c")
                                            val wind_hour = hour_obj.getString("wind_mph")
                                            val condition_hour = hour_obj.getJSONObject("condition")
                                            val img_hour = condition_hour.getString("icon")
                                            val hour =
                                                Hour(
                                                    time,
                                                    temp_hour.substring(0, 2),
                                                    wind_hour,
                                                    img_hour
                                                )
                                            Log.d("TAG", "onDayClick: $time_now + $date_day")
                                            if (time_now == date_day) {
                                                hours.add(hour)

                                                var manager =
                                                    LinearLayoutManager(
                                                        requireContext(),
                                                        LinearLayoutManager.HORIZONTAL,
                                                        false
                                                    )

                                                binding.dailyHourRv.layoutManager = manager
                                                binding.dailyHourRv.adapter = HourAdapter(hours)

                                            }

                                        }
                                    }
                                })
                        }

                        val hour = resobj.getJSONArray("hour")
                        if (date_day == currentTime){
                            for (i in 0 until hour.length()){
                                val hour_obj = hour.getJSONObject(i)
                                val time_date = hour_obj.getString("time")
                                val time = time_date.substring(time_date.length-5,time_date.length)
                                val temp_hour = hour_obj.getString("temp_c")
                                val wind_hour = hour_obj.getString("wind_mph")
                                val condition_hour = hour_obj.getJSONObject("condition")
                                val img_hour = condition_hour.getString("icon")
                                val hour = Hour(time,temp_hour,wind_hour,img_hour)

                                hours.add(hour)
                                var manager =
                                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                                binding.dailyHourRv.layoutManager = manager
                                binding.dailyHourRv.adapter = HourAdapter(hours)



                            }
                        }


//                    Log.d("TAG", "onResponse: $hour")
                    }


                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        Log.d("TAG", "onErrorResponse: $error")
                    }

                })
                requestque.add(request)


            } else Toast.makeText(requireContext(), "you have not filled gap", Toast.LENGTH_SHORT)
                .show()

        }
        requestque.add(request)




        return binding.root
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Weather2Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Weather2Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}