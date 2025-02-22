package com.example.onestopaway

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.onestopaway.placeholder.PlaceholderContent.PlaceholderItem
import com.example.onestopaway.databinding.FragmentStopsBinding

/**
 * [RecyclerView.Adapter] that can display a Stop.
 * TODO: Replace the implementation with code for your data type.
 */
class StopRecyclerViewAdapter(
    private val values: List<Stop>
) : RecyclerView.Adapter<StopRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("OneStopAway", "${values.size}")
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
        holder.numberView.text = item.id.toString()

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentStopsBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameView: TextView = binding.stopName
        val numberView: TextView = binding.stopNumber

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

}