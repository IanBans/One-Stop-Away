package com.example.onestopaway

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, "database", null, 1) {


    companion object {

        const val STOP_TABLE_NAME = "STOP"
        const val TRIP_TABLE_NAME = "TRIP"
        const val ROUTE_TABLE_NAME = "ROUTE"

        //singleton for the database manager
        @Volatile
        private var INSTANCE : DatabaseManager? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): DatabaseManager =
            INSTANCE ?: synchronized(this) {
                val newDB = INSTANCE ?:DatabaseManager(context).also { INSTANCE = it }
                newDB
            }
    }

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE IF NOT EXISTS $STOP_TABLE_NAME(${Stop.ID_COL}, ${Stop.NUMBER_COL},${Stop.NAME_COL}," +
                " ${Stop.LAT_COL}, ${Stop.LONG_COL}, ${Stop.FAV_COL})")
        db?.execSQL("CREATE TABLE IF NOT EXISTS $TRIP_TABLE_NAME(${Trip.TRIP_ID_COL}, ${Trip.NAME_COL}, ${Trip.FAV_COL})")
        db?.execSQL("CREATE TABLE IF NOT EXISTS $ROUTE_TABLE_NAME(${Route.ROUTE_ID_COL}, ${Route.ARRIVAL_TIME_COL}, " +
                "${Route.DEP_TIME_COL}, ${Stop.ID_COL})")
    }

    fun clearDBAndRecreate() {
        writableDatabase.execSQL("DROP TABLE IF EXISTS $STOP_TABLE_NAME")
        writableDatabase.execSQL("DROP TABLE IF EXISTS $ROUTE_TABLE_NAME")
        writableDatabase.execSQL("DROP TABLE IF EXISTS $TRIP_TABLE_NAME")
        onCreate(writableDatabase)
        writableDatabase.close()
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    //INSERT TO TABLES

    fun insertStop(id: Int, number: Int, name: String, lat: String, long: String, fav: Int){
        val values = ContentValues()
        values.put(Stop.ID_COL, id)
        values.put(Stop.NUMBER_COL, number)
        values.put(Stop.NAME_COL, name)
        values.put(Stop.LAT_COL, lat)
        values.put(Stop.LONG_COL, long)
        values.put(Stop.FAV_COL, fav)
        writableDatabase.insertWithOnConflict(STOP_TABLE_NAME, null, values, CONFLICT_REPLACE)

    }

    fun insertTrip(id: Int, head: String, fav: Int){
        val values = ContentValues()
        values.put(Trip.NAME_COL, head)
        values.put(Trip.TRIP_ID_COL, id)
        values.put(Trip.FAV_COL, fav)
        writableDatabase.insertWithOnConflict(TRIP_TABLE_NAME, null, values, CONFLICT_REPLACE)


    }

    fun insertRoute(id: Int, at: String, dt: String, stop: Int){
        val values = ContentValues()
        values.put(Route.ROUTE_ID_COL, id)
        values.put(Route.ARRIVAL_TIME_COL, at)
        values.put(Route.DEP_TIME_COL, dt)
        values.put(Stop.ID_COL, stop)

        writableDatabase.insertWithOnConflict(ROUTE_TABLE_NAME, null, values, CONFLICT_REPLACE)


    }

    //READ FROM TABLES
    fun readAllStops(): List<List<String>>{
        val result = mutableListOf<List<String>>()

        val cursor = writableDatabase.rawQuery("SELECT * FROM $STOP_TABLE_NAME", null)
        while(cursor.moveToNext()){
            val row = mutableListOf<String>()

            row.add(cursor.getInt(0).toString())
            row.add(cursor.getString(1))
            row.add(cursor.getString(2))
            row.add(cursor.getString(3))
            row.add(cursor.getString(4))
            row.add(cursor.getString(5))

            result.add(row)
        }
        cursor.close()

        return result
    }

    fun readAllTrips(): List<List<String>>{
        val result = mutableListOf<List<String>>()

        val cursor = writableDatabase.rawQuery("SELECT * FROM $TRIP_TABLE_NAME", null)
        while(cursor.moveToNext()){
            val row = mutableListOf<String>()

            row.add(cursor.getInt(0).toString())
            row.add(cursor.getString(1))
            row.add(cursor.getString(2))

            result.add(row)
        }
        cursor.close()
        return result
    }

    fun readAllRoutes(): List<List<String>>{
        val result = mutableListOf<List<String>>()


        val cursor = writableDatabase.rawQuery("SELECT * FROM $ROUTE_TABLE_NAME", null)
        while(cursor.moveToNext()){
            val row = mutableListOf<String>()

            row.add(cursor.getInt(0).toString())
            row.add(cursor.getInt(1).toString())
            row.add(cursor.getString(2))
            row.add(cursor.getString(3))

            result.add(row)
        }
        cursor.close()

        return result
    }

    //Returns stop id based on stop name
    fun getStopID(name: String): Int{
        val id: Int
        val param = Array<String>(1){name}

        val cursor = writableDatabase.rawQuery("SELECT $STOP_TABLE_NAME.${Stop.ID_COL} FROM $STOP_TABLE_NAME WHERE $STOP_TABLE_NAME.${Stop.NAME_COL} = ?", param)
        cursor.moveToNext()
        id = cursor.getInt(0)
        cursor.close()
        return id
    }

    //Returns route id based on route name
    fun getTripID(name: String): Int{
        val id: Int
        val param = Array(1){name}
        val cursor = writableDatabase.rawQuery("SELECT $TRIP_TABLE_NAME.${Trip.TRIP_ID_COL} FROM $TRIP_TABLE_NAME WHERE $TRIP_TABLE_NAME.${Trip.NAME_COL} = ?", param)


        cursor.moveToNext()
        id = cursor.getInt(0)
        cursor.close()
        return id
    }

    //Gets stops based on route id
    fun getStopsOnRoute(id: Int): List<List<String>>{
        val result = mutableListOf<List<String>>()
        val param = Array<String>(1){id.toString()}

        val cursor = writableDatabase.rawQuery(
            "SELECT ${STOP_TABLE_NAME}.${Stop.NAME_COL} FROM $STOP_TABLE_NAME INNER JOIN $ROUTE_TABLE_NAME ON $STOP_TABLE_NAME.${Stop.ID_COL} = ${ROUTE_TABLE_NAME}.${Stop.ID_COL} WHERE ${ROUTE_TABLE_NAME}.${Stop.ID_COL} = ?",
            param)

        while(cursor.moveToNext()){
            result.add(listOf(cursor.getString(0)))
        }
        cursor.close()
        return result
    }

    fun getFavoriteStops() : List<List<String>> {
        val result = mutableListOf<List<String>>()
        val cursor = writableDatabase.rawQuery("SELECT * FROM $STOP_TABLE_NAME WHERE ${Stop.FAV_COL} = 1", null)
        while(cursor.moveToNext()){
            val row = mutableListOf<String>()

            row.add(cursor.getInt(0).toString())
            row.add(cursor.getString(1))
            row.add(cursor.getString(2))
            row.add(cursor.getString(3))
            row.add(cursor.getInt(4).toString())
            row.add(cursor.getString(5))

            result.add(row)
        }
        cursor.close()
        return result
    }

    //get all favorited trips
    fun getFavoriteTrips(): List<List<String>> {
        val result = mutableListOf<List<String>>()


        val cursor = writableDatabase.rawQuery("SELECT * FROM $TRIP_TABLE_NAME WHERE $TRIP_TABLE_NAME.${Trip.FAV_COL} = 1", null)
        while(cursor.moveToNext()){
            val row = mutableListOf<String>()

            row.add(cursor.getInt(0).toString())
            row.add(cursor.getInt(1).toString())
            row.add(cursor.getInt(2).toString())

            result.add(row)
        }
        cursor.close()
        return result
    }

    //get all arrival times for a single stop
    fun getArrivalTimesByStop(id: Int): List<String> {
        val result = mutableListOf<String>()
        val param = Array<String>(1){id.toString()}
        val cursor = writableDatabase.rawQuery("SELECT $ROUTE_TABLE_NAME.${Route.ARRIVAL_TIME_COL} FROM $ROUTE_TABLE_NAME WHERE ${Stop.ID_COL} = ?", param)

        while(cursor.moveToNext()){

            result.add(cursor.getString(0))
        }
        cursor.close()

        return result
    }

}
