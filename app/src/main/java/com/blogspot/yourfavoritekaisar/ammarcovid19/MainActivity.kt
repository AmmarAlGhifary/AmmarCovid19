package com.blogspot.yourfavoritekaisar.ammarcovid19

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.yourfavoritekaisar.ammarcovid19.adapter.CountryAdapter
import com.blogspot.yourfavoritekaisar.ammarcovid19.network.AllCountries
import com.blogspot.yourfavoritekaisar.ammarcovid19.network.ApiService
import com.blogspot.yourfavoritekaisar.ammarcovid19.network.Countries
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var countryAdapter: CountryAdapter
    private var ascending = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCountry()
        btn_sequence.setOnClickListener {
            sequenceListener(ascending)
            ascending = !ascending
        }

        search_view.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                countryAdapter.filter.filter(newText)
                return false
            }
        })

        swipe_refresh.setOnRefreshListener {
            getCountry()
            swipe_refresh.isRefreshing = true
        }
    }

    private fun sequenceListener(ascending: Boolean) {
        rv_country.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            if (ascending) {
                (layoutManager as LinearLayoutManager).reverseLayout = true
                (layoutManager as LinearLayoutManager).stackFromEnd = true
            } else {
                (layoutManager as LinearLayoutManager).reverseLayout = false
                (layoutManager as LinearLayoutManager).stackFromEnd = false
                Toast.makeText(this@MainActivity, "Z-A", Toast.LENGTH_SHORT).show()
            }
            adapter = countryAdapter

        }
    }

    private fun getCountry() {
        val okHttp = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder().baseUrl("https://api.covid19api.com/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.getAllCountry().enqueue(object : Callback<AllCountries> {
            override fun onFailure(call: Call<AllCountries>, t: Throwable) {

            }

            override fun onResponse(call: Call<AllCountries>, response: Response<AllCountries>) {
                if (response.isSuccessful) {
                    val dataCovid = response.body()?.Global

                    val formatter: NumberFormat = DecimalFormat("#,###")
                    tv_confirmed_globe.text = formatter.format(dataCovid!!.TotalConfirmed.toDouble())
                    tv_deaths_globe.text = formatter.format(dataCovid.TotalDeaths.toDouble())
                    tv_recovered_globe.text = formatter.format(dataCovid.TotalRecovered.toDouble())

                    rv_country.apply {
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        setHasFixedSize(true)
                        countryAdapter =
                            CountryAdapter(
                                response.body()?.Countries as ArrayList<Countries>
                            ) {
                                itemClicked(it)
                            }

                        countryAdapter = countryAdapter
                        progress_bar.visibility = View.GONE
                    }
                } else {
                    progress_bar.visibility = View.GONE
                    handleError(this@MainActivity)
                }
            }
        })
    }

    private fun itemClicked(it: Countries) {
        val intent = Intent(this, CountryChartActivity::class.java)
        intent.putExtra(CountryChartActivity.EXTRA_COUNTRY, it.Country)
        intent.putExtra(CountryChartActivity.EXTRA_COUNTRY_CODE, it.CountryCode)

        intent.putExtra(CountryChartActivity.EXTRA_LATEST_UPDATE, it.Date)
        intent.putExtra(CountryChartActivity.EXTRA_NEW_DEATH, it.NewDeaths)

        intent.putExtra(CountryChartActivity.EXTRA_NEW_CONFIRMED, it.NewConfirmed)
        intent.putExtra(CountryChartActivity.EXTRA_NEW_RECOVERED, it.NewRecovered)
        intent.putExtra(CountryChartActivity.EXTRA_NEW_DEATHS, it.NewDeaths)

        intent.putExtra(CountryChartActivity.EXTRA_TOTAL_CONFIRM, it.TotalConfirmed)
        intent.putExtra(CountryChartActivity.EXTRA_TOTAL_RECOVERED, it.TotalRecovered)
        intent.putExtra(CountryChartActivity.EXTRA_TOTAL_DEATHS, it.TotalDeaths)
        startActivity(intent)
    }

    private fun handleError(mainActivity: MainActivity) {
        TODO("Not yet implemented")
    }
}