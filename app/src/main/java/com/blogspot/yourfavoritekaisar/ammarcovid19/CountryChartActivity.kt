package com.blogspot.yourfavoritekaisar.ammarcovid19

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.yourfavoritekaisar.ammarcovid19.network.InfoCountry
import com.blogspot.yourfavoritekaisar.ammarcovid19.network.InfoService
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.activity_country_chart.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class CountryChartActivity : AppCompatActivity() {

    private var sharedPrefFile = "kotlinsharedpreference"
    private lateinit var sharedPreference: SharedPreferences
    private var dayCases = ArrayList<String>()

    companion object {

        const val EXTRA_COUNTRY = "extra_country"
        const val EXTRA_COUNTRY_CODE = "extra_country_code"
        const val EXTRA_LATEST_UPDATE = "extra_latest_update"
        const val EXTRA_NEW_DEATH = "extra_new_death"
        const val EXTRA_NEW_CONFIRMED = "extra_new_confirmed"
        const val EXTRA_NEW_RECOVERED = "extra_new_recovered"
        const val EXTRA_NEW_DEATHS = "extra_new_deaths"
        const val EXTRA_TOTAL_CONFIRM = "extra_total_confirm"
        const val EXTRA_TOTAL_RECOVERED = "extra_total_recovered"
        const val EXTRA_TOTAL_DEATHS = "extra_total_deaths"

        lateinit var dataCountry: String
        lateinit var dataFlag: String
    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_chart)

        sharedPreference = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        val country = intent.getStringExtra(EXTRA_COUNTRY)
        val date = intent.getStringExtra(EXTRA_LATEST_UPDATE)
        val countryCode = intent.getStringExtra(EXTRA_COUNTRY_CODE)
        val newDeath = intent.getStringExtra(EXTRA_NEW_DEATH)
        val newConfirmed = intent.getStringExtra(EXTRA_NEW_CONFIRMED)
        val newRecover = intent.getStringExtra(EXTRA_NEW_RECOVERED)
        val totalDeath = intent.getStringExtra(EXTRA_TOTAL_DEATHS)
        val totalConfirmed = intent.getStringExtra(EXTRA_TOTAL_CONFIRM)
        val totalrRecover = intent.getStringExtra(EXTRA_TOTAL_RECOVERED)

        val formatter: NumberFormat = DecimalFormat("#,###")
        txt_country_chart.text = country
        txt_current.text = date

        txt_total_confirmed_current.text = formatter.format(totalConfirmed?.toDouble())
        txt_new_confirmed_current.text = formatter.format(newConfirmed?.toDouble())
        txt_total_deaths_current.text = formatter.format(totalDeath?.toDouble())

        txt_new_deaths_current.text = formatter.format(newDeath?.toDouble())
        txt_total_recovered_current.text = formatter.format(totalrRecover?.toDouble())
        txt_new_recovered_current.text = formatter.format(newRecover?.toDouble())

        val editor: SharedPreferences.Editor = sharedPreference.edit()
        editor.putString(country, country)
        editor.apply()
        editor.commit()

        val saveDataCountry = sharedPreference.getString(country, country)
        val saveCountryFlag = sharedPreference.getString(countryCode, countryCode)

        dataCountry = saveDataCountry.toString()
        dataFlag = saveCountryFlag.toString() + "/flat/64.png"

        if (saveCountryFlag != null) {
            Glide.with(this).load("https://www.countryflags.io/$dataFlag")
                .into(img_flag_chart)
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT)
        }

        getCountry()
    }

    private fun getCountry() {
        val okHttp = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/dayone/country/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(InfoService::class.java)
        api.getInfoService(dataCountry).enqueue(object : Callback<List<InfoCountry>> {
            override fun onFailure(call: Call<List<InfoCountry>>, t: Throwable) {
                TODO("Not yet implemented")
            }

            @SuppressLint("SimpleDateFormat")
            override fun onResponse(
                call: Call<List<InfoCountry>>,
                response: Response<List<InfoCountry>>
            ) {
                if (response.isSuccessful) {
                    val dataCovid = response.body()
                    val barEntries1: ArrayList<BarEntry> = ArrayList()
                    val barEntries2: ArrayList<BarEntry> = ArrayList()
                    val barEntries3: ArrayList<BarEntry> = ArrayList()
                    val barEntries4: ArrayList<BarEntry> = ArrayList()
                    var i = 0

                    while (i < dataCovid?.size ?: 0) {
                        for (s in dataCovid!!) {
                            val barEntry1 = BarEntry(i.toFloat(), s.Confirmed.toFloat())
                            val barEntry2 = BarEntry(i.toFloat(), s.Deaths.toFloat())
                            val barEntry3 = BarEntry(i.toFloat(), s.Recovered.toFloat())
                            val barEntry4 = BarEntry(i.toFloat(), s.Active.toFloat())

                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'")
                            val outputFormat = SimpleDateFormat("dd-MM-yyy")
                            val date: Date? = inputFormat.parse(s.Date)
                            val formattedDate: String = outputFormat.format(date!!)
                            dayCases.add(formattedDate)

                            barEntries1.add(barEntry1)
                            barEntries2.add(barEntry2)
                            barEntries3.add(barEntry3)
                            barEntries4.add(barEntry4)

                            i++
                        }
                    }

                    val xAxis: XAxis = chart_view.xAxis
                    xAxis.valueFormatter = IndexAxisValueFormatter(dayCases)
                    chart_view.axisLeft.axisMinimum = 0f
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f
                    xAxis.setCenterAxisLabels(true)
                    xAxis.isGranularityEnabled = true


                    val barDataSet1 = BarDataSet(barEntries1, "Confirmed")
                    val barDataSet2 = BarDataSet(barEntries2, "Deaths")
                    val barDataSet3 = BarDataSet(barEntries3, "Recovered")
                    val barDataSet4 = BarDataSet(barEntries4, "Active")
                    barDataSet1.color = Color.parseColor("#F44336")
                    barDataSet2.color = Color.parseColor("#FFEB3B")
                    barDataSet3.color = Color.parseColor("#03DAC5")
                    barDataSet4.color = Color.parseColor("#2196F3")

                    val data = BarData(barDataSet1, barDataSet2, barDataSet3, barDataSet4)
                    chart_view.data = data

                    val barSpace = 0.02f
                    val groupSpace = 0.3f
                    val groupCount = 4f

                    data.barWidth = 0.15f
                    chart_view.invalidate()
                    chart_view.setNoDataTextColor(android.R.color.black)
                    chart_view.setTouchEnabled(true)
                    chart_view.description.isEnabled = false
                    chart_view.xAxis.axisMinimum = 0f
                    chart_view.setVisibleXRangeMaximum(
                        0f + chart_view.barData.getGroupWidth(
                            groupSpace,
                            barSpace
                        ) * groupCount
                    )
                    chart_view.groupBars(0f, groupSpace, barSpace)
                }
            }
        })
    }
}

