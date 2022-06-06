package com.example.onestopaway

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class DatabaseManagerTest {
    private lateinit var db : DatabaseManager
    private lateinit var repository: DataRepository


    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = DatabaseManager.getDatabase(appContext)
        db.clearDBAndRecreate()
        repository = DataRepository(db)

    }

    @Test
    @ExperimentalCoroutinesApi
    fun  stopsAreAdded() = runTest {
        repository.populateStops()
        assertTrue(repository.numStopsFetched > 1)
        assertEquals(repository.readAllStops().size, repository.numStopsFetched)

    }

    @Test
    @ExperimentalCoroutinesApi
    fun tripsAreAdded() = runTest  {
        repository.populateTrips()
        assertTrue(repository.numTripsFetched > 1)
        assertEquals(repository.readAllTrips().size, repository.numTripsFetched)

    }

    @Test
    @ExperimentalCoroutinesApi
    fun routesAreAdded() = runTest {
        repository.populateRoutes()
        assertTrue(repository.numRoutesFetched > 1)
        assertEquals(db.readAllRoutes().size, repository.numRoutesFetched)

    }

    @Test
    fun correctRouteID() = runTest {
        repository.populateRoutes()
        val test = db.getRouteID("331 Cordata/WCC")

        assertEquals(test, 2028020)
    }

    @Test
    fun favoriteStops()  {
        val testStop = Stop(113, 2456,"Test Stop", 44.44, 44.44,1 )
        db.insertStop(testStop.id, testStop.number, testStop.name, testStop.latitude.toString(),
                testStop.longitude.toString(), testStop.isFavorite.toInt())

        val result = db.getFavoriteStops()

        assertEquals(result.size, 1)
    }

    @Test
    fun correctStop() {
        val test = db.getStopID("Bakerview Rd at Fred Meyer")

        assertEquals(test, 4)
    }

    @Test

    fun correctTimes()  {
        val test = db.getArrivalTimesByStop(153)

        assertEquals(test[0][0], "16:08:00")
    }

    @Test
    @ExperimentalCoroutinesApi
    fun favoriteTrips() {
        db.insertTrip(44444, "334 Test/Trip",1)

        val result = db.getFavoriteTrips()

        assertEquals(result[0][0].toInt(), 44444)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun getClosestArrivalTimesByStop() {
        val result = db.getArrivalTimesByStop(3)

        Log.d("Testing", result.toString())
        assertTrue(result.isNotEmpty())

    }


    @After
    fun tearDown() {
        db.close()
    }
}
