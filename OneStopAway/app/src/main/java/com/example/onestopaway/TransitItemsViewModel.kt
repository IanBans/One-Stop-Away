// Nicholas J Uhlhorn
// May 2022
// CSCI 412
package com.example.onestopaway

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.LocalTime

class TransitItemsViewModel(private val repository: DataRepository): ViewModel() {

    init {
        populateAll()
    }
    private var _stops = mutableListOf<Stop>()
    private var _trips = mutableListOf<Trip>()



    // Getters and Setters
    val stops
        get() = _stops

    val trips
        get() = _trips

    /**
     * Populates _stops and _routes based on the given keyword
     * @param keyword The keyword to be used by the search
     */
    fun keywordSearch(keyword: String){
        // TODO: Implement this function
    }

    /**
     * Populates _stops and _routes with all routes and stops
     */
    fun populateAll() = viewModelScope.launch(Dispatchers.IO){
        // Reset the stop and route list
        _stops = repository.readAllStops().toMutableList()
        _trips = repository.readAllTrips().toMutableList()

    }

    fun populateTrips() = viewModelScope.launch{
        _trips = repository.readAllTrips().toMutableList()
    }

    /**
     * A Helper function that tells all stops to update their next arrival times
     */
    fun updateStopArrivalTimes() = viewModelScope.launch {
        _stops.forEach {
            val times : List<String> = repository.getAllTripsByStop(it.id)
            it.updateTimeUntilNextBus(times)
        }
    }

    /**
     * Populates _stops and _routes based on given location
     * @param latitude the latitude to search from
     * @param longitude the longitude to search from
     * @param maxDistance the maximum distance a stop can be from the location in miles
     */
    fun getClosestStops(latitude: Double, longitude: Double, maxDistance: Double) = viewModelScope.launch(Dispatchers.IO) {
        // Reset the stop and route list
        _stops = mutableListOf()
        _trips = mutableListOf()
        val stops = repository.readAllStops()


        // Get all stops and routes from the database

        // convert and add each stop to _stops
        stops.forEach { currStop ->
            // Make stop from row
            // If the stop is in range add it to the list
            if(currStop.getDistance(latitude, longitude) <= maxDistance){
                _stops.add(currStop)
            }

        }


    }

    fun getClosestTrips(latitude: Double, longitude: Double, maxDistance: Double) = viewModelScope.launch(Dispatchers.IO) {
        val trips = repository.readAllTrips()

        trips.forEach { newTrip ->
            // If the route has one of the stops listed then add it
            // NOTE: This might be costly...
            var added: Boolean = false
            for (tripStop in newTrip.stops) {
                for (stop in _stops) {
                    if (stop.compareStop(tripStop)) {
                        _trips.add(newTrip)
                        added = true
                    }
                    if (added) {
                        break
                    }
                }
                if (added) {
                    break
                }
            }
        }
    }
    fun populateDatabase() = viewModelScope.launch(Dispatchers.IO) {
        repository.populateDatabase()
    }

    /**
     * Populates _stops and _routes based on the stops and routes that are favorites
     */
    fun populateFavorites() = viewModelScope.launch(Dispatchers.IO){
        // Reset the stop and route list
        _stops = repository.getFavoriteStops() as MutableList<Stop>
        _trips = repository.getFavoriteTrips() as MutableList<Trip>
    }

    /**
     * Populates _stops and _routes based on predetermined lists of stops and routes
     * @param stops Preexisting list of Stops
     * @param trips Preexisting list of Routes
     */
    fun preexistingPopulate(stops: MutableList<Stop>, trips: MutableList<Trip>){
        _stops = stops
        _trips = trips
    }

}

class TransitItemsViewmodelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransitItemsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransitItemsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
