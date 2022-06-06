package com.example.onestopaway

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

/**
 * A fragment representing a list of Items.
 */
class RouteListFragment : Fragment() {

    private var columnCount = 1
    private val viewModel : TransitItemsViewModel by activityViewModels {
        TransitItemsViewmodelFactory((requireActivity().application as OneBusAway).repository)}
    private lateinit var trips : List<Trip>
    private lateinit var listener : Listener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        

        viewModel.populateTrips()
        trips = viewModel.trips

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_route_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                adapter = RouteRecyclerViewAdapter(trips)
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(lr : Listener) =
            RouteListFragment().apply {
                listener = lr
            }
    }
}