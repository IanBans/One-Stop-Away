package com.example.onestopaway

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.onestopaway.databinding.FragmentStopsBinding

/**
 * [RecyclerView.Adapter] that can display a Stop.
 * TODO: Replace the implementation with code for your data type.
 */
class StopRecyclerViewAdapter(
    private val values: List<Stop>, private val listener : Listener
) : RecyclerView.Adapter<StopRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentStopsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.numberView.text = item.number.toString()
        if(item.minutesToNextBus >= 0) {
            holder.nextBusView.text = "Next Bus in ${item.minutesToNextBus} minutes"
        }else{
            holder.nextBusView.text = "No bus this hour"
        }
        holder.nameView.setOnClickListener {
            listener.onStopClicked(values[position])
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentStopsBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameView: TextView = binding.stopName
        val numberView: TextView = binding.stopNumber
        val nextBusView: TextView = binding.nextBusTime

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

}

