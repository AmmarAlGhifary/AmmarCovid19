package com.blogspot.yourfavoritekaisar.ammarcovid19.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.yourfavoritekaisar.ammarcovid19.R
import com.blogspot.yourfavoritekaisar.ammarcovid19.network.Countries
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_country.view.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CountryAdapter (val country: ArrayList<Countries>, private val clickListener : (Countries) -> Unit) :
        RecyclerView.Adapter<CountryAdapter.ViewHolder>(), Filterable{

    var countryFilterList = ArrayList<Countries>()

    init {
        countryFilterList = country
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_country,
                parent,
                false
            )
        )

    override fun getItemCount() = countryFilterList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(countryFilterList[position], clickListener)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                countryFilterList = if (charSearch.isEmpty()) {
                    country
                } else {
                    val resultList = ArrayList<Countries>()
                    for (row in country) {
                        if (row.Country.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))){
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResult = FilterResults()
                filterResult.values = countryFilterList
                return filterResult
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as ArrayList<Countries>
                notifyDataSetChanged()
            }
        }
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCountry: TextView = itemView.txt_country_name
        private val tvTotalCase: TextView = itemView.txt_total_case
        private val tvTotalRecovered: TextView = itemView.txt_total_recovered
        private val tvTotalDeath: TextView = itemView.txt_total_deaths
        private val flag: ImageView = itemView.img_flag_country

        fun bindItem(countries: Countries, clickListener: (Countries) -> Unit) {
            val formatter: NumberFormat = DecimalFormat("#,###")
            tvCountry.txt_country_name.text = countries.Country
            tvTotalCase.txt_total_case.text = formatter.format(countries.TotalConfirmed.toDouble())
            tvTotalRecovered.txt_total_recovered.text =
                formatter.format(countries.TotalRecovered.toDouble())
            tvTotalDeath.txt_total_deaths.text = formatter.format(countries.TotalDeaths.toDouble())

            Glide.with(itemView)
                .load("https://www.countryflags.io/" + countries.CountryCode + "/flat/16.png")
                .into(flag)

            itemView.setOnClickListener { clickListener(countries) }
        }
    }
}