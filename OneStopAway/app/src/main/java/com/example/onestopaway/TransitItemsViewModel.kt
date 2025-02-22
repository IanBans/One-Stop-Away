// Nicholas J Uhlhorn
// May 2022
// CSCI 412
package com.example.onestopaway

import android.content.Context
import androidx.lifecycle.ViewModel

class TransitItemsViewModel(context: Context): ViewModel() {

    private var _stops = mutableListOf<Stop>()
    private var _routes = mutableListOf<Route>()

    private val _databaseManager = DatabaseManager.getDatabase(context)

    // Getters and Setters
    val stops
        get() = _stops

    val routes
        get() = _routes

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
    fun getAll(){
        // Reset the stop and route list
        _stops = mutableListOf<Stop>()
        _routes = mutableListOf<Route>()

        // Get all stops and routes from the database
        val stopStrings = _databaseManager.readAllStops()
        val routeStrings = _databaseManager.readAllRoutes()

        // convert and add each stop to _stops
        stopStrings.forEach {
            // Make stop from row
            _stops.add(makeStopFromDB(it))
        }
    }

    /**
     * Populates _stops and _routes based on given location
     * @param latitude the latitude to search from
     * @param longitude the longitude to search from
     * @param maxDistance the maximum distance a stop can be from the location in miles
     */
    fun distanceSearch(latitude: Double, longitude: Double, maxDistance: Double) {
        // Reset the stop and route list
        _stops = mutableListOf<Stop>()
        _routes = mutableListOf<Route>()

        // Get all stops and routes from the database
        val stopStrings = _databaseManager.readAllStops()
        val routeStrings = _databaseManager.readAllRoutes()

        // convert and add each stop to _stops
        stopStrings.forEach {
            // Make stop from row
            val newStop = makeStopFromDB(it)

            // If the stop is in range add it to the list
            if(newStop.getDistance(longitude, latitude) <= maxDistance){
                _stops.add(newStop)
            }
        }

        routeStrings.forEach {
            // Make new route from row
            val newRoute = makeRouteFromDB(it)

            // If the route has one of the stops listed then add it
            // NOTE: This might be costly...
            var added: Boolean = false
            for (routeStop in newRoute.stops) {
                for (stop in _stops){
                    if(stop.compareStop(routeStop)){
                        _routes.add(newRoute)
                        added = true
                    }
                    if(added){
                        break
                    }
                }
                if(added){
                    break
                }
            }
        }

    }

    /**
     * Populates _stops and _routes based on predetermined lists of stops and routes
     * @param stops Preexisting list of Stops
     * @param routes Preexisting list of Routes
     */
    fun preexistingPopulate(stops: MutableList<Stop>, routes: MutableList<Route>){
        _stops = stops
        _routes = routes
    }

    /**
     * A Helper function that takes in the database manager return of a stop and returns a Stop
     * @param stopData A List<String> of the stop data
     * @return Stop created from the Database data
     */
    fun makeStopFromDB(stopData: List<String>): Stop{
        val stopId =        stopData[0].toInt()
        val stopNum =       stopData[1].toInt()
        val stopName =      stopData[2]
        val stopLatitude =  stopData[3].toDouble()
        val stopLongitude = stopData[4].toDouble()
        val stopFavorited = stopData[5].toShort()

        // Make Stop and return
        return Stop(stopId, stopNum, stopName, stopLatitude, stopLongitude, stopFavorited)
    }

    /**
     * A Helper function that takes in the database manager return of a route and returns a Route
     * @param stopData A List<String> of the stop data
     * @return Route created from the Database data
     */
    fun makeRouteFromDB(routeData: List<String>): Route{
        val routeId =   routeData[0].toInt()
        val routeName = routeData[1]

        // Get Route Stops
        val stopData = _databaseManager.getStopsOnRoute(routeId)
        val routeStops = mutableListOf<Stop>()
        _databaseManager.close()

        stopData.forEach {
            // Make stop from row
            routeStops.add(makeStopFromDB(it))
        }

        // Make route and return
        return Route(routeId, routeName, routeStops)
    }


}