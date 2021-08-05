package com.example.pokemon

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pokemon.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkPermission()
    }
    // code to access location permission and this code to specialize the permission
    val accessLocation = 123
    // To access permission to get current location
    // 1--> Make fun to check permission
    fun checkPermission(){
        // 1--> check mini SDK
        if (Build.VERSION.SDK_INT >= 23){
            // 1--> Check if the permission is ordered before or not
            // PERMISSION_GRANTED mean that if you don't agree permission الا الان
            if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                // If need a permissions , you should order it from user
                // And you will order it by fun requestPermissions(pass all permissions you need her)
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),accessLocation)
                // If user accept or not accept onRequestPermissionResult will called
                return
            }
        }

        getUserLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        // make when() test on requestCode for check if this requestCode is == accessLocation code that i accully need in location permission
        when(requestCode){
            accessLocation -> {
                // test if user accept or deny the permission
                // grantResults is array of result of permissions
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }else
                {
                    Toast.makeText(this,"Permission deny",Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // 2--> Make fun to get current location if checkPermission is true
    fun getUserLocation(){
        Toast.makeText(this,"Location access now",Toast.LENGTH_SHORT).show()
        //TODO: access user current location

        // Instance from MyLocation inner class
        val mLocation =MyLocationListener()
        //Manager to run location service
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // run this service
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,mLocation)

        val myThread = MyThread()
        myThread.start()

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(myLocation!!.latitude, myLocation!!.longitude)
        mMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Me")
            .snippet("here is my location")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,8f))
    }
    // variable to location
    var myLocation:Location?= null
    // new class to access location by GPS
    inner class MyLocationListener:LocationListener{
        constructor(){
            // initial value to my location
            myLocation=Location("Me")
            myLocation!!.longitude=0.0
            myLocation!!.latitude=0.0
        }
        override fun onLocationChanged(location: Location) {
            TODO("Not yet implemented")
            myLocation=location
        }
    }

    // new class to view location data in map
    inner class MyThread:Thread{
        constructor():super(){
            //  TODO: set old location
        }

        //run fun don't go to UI ok
        // i will use runOnUiThread fun on run
        override fun run() {
            // I need to view user location constantly
            while (true){
                try {
                    mMap!!.clear()
                //while true mean constantly
                // take user location data and view it in map in screen constantly
               runOnUiThread() {
                   // Add a marker in Sydney and move the camera
                   val sydney = LatLng(myLocation!!.latitude, myLocation!!.longitude)
                   mMap.addMarker(
                       MarkerOptions()
                           .position(sydney)
                           .title("Me")
                           .snippet("here is my location")
                   )
                   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 8f))
               }
                    Thread.sleep(1000)
                }catch (ex:Exception){}

            }

        }
    }
}