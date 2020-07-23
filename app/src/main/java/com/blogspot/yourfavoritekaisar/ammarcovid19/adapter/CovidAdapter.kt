package com.blogspot.yourfavoritekaisar.ammarcovid19.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.yourfavoritekaisar.ammarcovid19.R
import com.blogspot.yourfavoritekaisar.ammarcovid19.network.Countries

class CovidAdapter(val country: ArrayList<Countries>, val clickListener: (Countries) -> Unit) :
    RecyclerView.Adapter<CovidAdapter.ViewHolder>(), Filterable {


    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_country, parent, false)
        )

    override fun getItemCount() = country.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(itemView: View){

        }
    }


}