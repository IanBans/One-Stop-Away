package com.example.onestopaway

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.onestopaway.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(), Listener {

    private var _binding : ActivityMainBinding? = null
    private val viewModel : TransitItemsViewModel by viewModels { TransitItemsViewmodelFactory((application as OneBusAway).repository)}
    private val binding get() = _binding!!
    private var currentLoc: LatLng = LatLng(48.73280011832849,-122.48508132534693)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create ViewModel
        //add the initial route list fragment

        binding.menuBar.setOnItemSelectedListener {
            onOptionsItemSelected(it)
        }

        if(savedInstanceState == null) {
            viewModel.populateDatabase()
            val route = RouteListFragment.newInstance(this)
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.main_page_container, route)
                commit()
            }

        } else {
            //get the current menu item and recreate that fragment on reinstation
           val itemid = savedInstanceState.getInt("MENU_ITEM")
            when(itemid) {
                R.id.routes -> {
                        // Populate nearest routes
                        val newFrag = RouteListFragment.newInstance(this)
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.main_page_container, newFrag)
                            commit()
                        }
                    }
                R.id.favorites -> {
                        val newFrag = FavoritesFragment()
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.main_page_container, newFrag)
                            commit()
                        }
                }
                R.id.stops -> {
                        val newFrag = StopsListFragment.newInstance(this, currentLoc, true)
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.main_page_container, newFrag)
                            commit()
                        }

                }
            }
        }
        getLocation()


    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("MENU_ITEM", binding.menuBar.selectedItemId)
        super.onSaveInstanceState(outState)
    }


    // clicklistener for the bottom menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.routes -> {
                    val frag = supportFragmentManager.findFragmentById(R.id.main_page_container)
                    if(frag is RouteListFragment) {
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.main_page_container, frag)
                            commit()
                        }
                    } else {
                        // Populate nearest routes
                        val newFrag = RouteListFragment.newInstance(this)
                        supportFragmentManager.beginTransaction().apply {
                            replace(R.id.main_page_container, newFrag)
                            commit()
                        }
                    }
                Log.d("BUTTONLOG", "clicked route tab")
                true
            }
            R.id.favorites -> {
                val frag = supportFragmentManager.findFragmentById(R.id.main_page_container)
                if(frag is FavoritesFragment) {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.main_page_container, frag)
                        commit()
                    }
                } else {
                    // Populate ViewModel with favorites

                    val newFrag = FavoritesFragment.newInstance(this, currentLoc)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.main_page_container, newFrag)
                        commit()
                    }
                }
                Log.d("BUTTONLOG", "clicked favorites tab")
                true
            }
            R.id.stops -> {
                val data : List<Stop> = viewModel.stops
                val frag = supportFragmentManager.findFragmentById(R.id.main_page_container)

                if(frag is StopsListFragment) {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.main_page_container, frag)
                        commit()
                    }
                } else {
                    // Populate nearest stops
                    val newFrag = StopsListFragment.newInstance(this, currentLoc, true)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.main_page_container, newFrag)
                        commit()
                    }
                }
                Log.d("BUTTONLOG", "clicked stop tab")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

@SuppressLint("MissingPermission")
// gets the user location
fun getLocation(): LatLng {
    val client = LocationServices.getFusedLocationProviderClient(this)
    var location : LatLng?
    if(checkSelfPermission(ACCESS_FINE_LOCATION) != PERMISSION_GRANTED ||
        checkSelfPermission(ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), 1)
    } else {
        client.lastLocation.addOnSuccessListener {
                currentLoc = LatLng(it.latitude, it.longitude)
                Log.d("LOC", currentLoc.toString())
        }
    }
    return currentLoc
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if(requestCode == 1) {
        getLocation()
    }
}

    override fun onStopClicked(stop: Stop) {
        Intent(this, StopDetailActivity::class.java).apply {
            this.putExtra("passed_stop", stop)
            getLocation()
            this.putExtra("currentLat", currentLoc.latitude)
            this.putExtra("currentLong", currentLoc.longitude)
            startActivity(this)

        }
    }


}