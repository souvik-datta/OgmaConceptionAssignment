package com.souvik.assignmentogma

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsFragment : Fragment() {

    private var gMap : GoogleMap? = null
    private val LOCATION_REQUEST = 200
    private lateinit var mLocationCallback: LocationCallback
    private val title: String = ""
    private var marker : Marker? = null
    private var polygon : Circle? = null

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap
        googleMap.isMyLocationEnabled = true
        permissionCheck()
        /*val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onResume() {
        super.onResume()
        permissionCheck()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionCheck() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // You can use the API that requires the permission.
            Log.d("TAG", "Permission granted..")
            fetchLocation()

        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST
            )
        }
    }

    //Fetching current user location.
    fun fetchLocation() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.apply {
            this.interval = 60000
            this.fastestInterval = 5000
            this.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.d(
                    "TAG",
                    "Latitude: ${locationResult.lastLocation.latitude} and Longitude: ${locationResult.lastLocation.longitude}"
                )
                if(marker == null)
                    gMap?.addMarker(MarkerOptions().position(LatLng(locationResult.lastLocation.latitude,locationResult.lastLocation.longitude)).title(MainActivity.title))
                else
                    marker!!.title = MainActivity.title
                gMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(locationResult.lastLocation.latitude,locationResult.lastLocation.longitude), 12.0f
                    )
                )
                if(polygon == null)
                    gMap!!.addCircle(
                        CircleOptions()
                            .center(LatLng(locationResult.lastLocation.latitude,locationResult.lastLocation.longitude))
                            .radius(10.0)
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE)
                    )
                LocationServices.getFusedLocationProviderClient(requireActivity())
                    .removeLocationUpdates(mLocationCallback)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //In case location permission is not given.
            return
        }
        LocationServices.getFusedLocationProviderClient(requireActivity())
            .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            fetchLocation()
        }else{
            Toast.makeText(requireContext(), "No location permission!", Toast.LENGTH_SHORT).show()
        }
    }
}

