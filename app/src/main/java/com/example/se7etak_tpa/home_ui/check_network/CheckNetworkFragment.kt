package com.example.se7etak_tpa.home_ui.check_network

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.databinding.FragmentCheckNetworkBinding
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class CheckNetworkFragment : Fragment() {

    /**
     * Egypt start latitude
     */
    private val EGYPT_START_LAT = 22

    /**
     * Egypt start longitude
     */
    private val EGYPT_START_LONG = 24

    /**
     * Egypt end latitude
     */
    private val EGYPT_END_LAT = 32

    /**
     * Egypt end longitude
     */
    private val EGYPT_END_LONG = 37

    /**
     * parameter of tile size
     * where each tile is a square with length 4h
     */
    private val h = 0.01

    /**
     * number of tiles rows in Egypt map
     */
    private val NO_OF_ROWS = ((EGYPT_END_LAT - EGYPT_START_LAT) / (4 * h)).toInt() + 1

    /**
     * number of tiles columns in Egypt map
     */
    private val NO_OF_COLUMNS = ((EGYPT_END_LONG - EGYPT_START_LONG) / (4 * h)).toInt() + 1

    /**
     * to keep track of tile changes
     */
    private var previousTile: Long = -1


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    /**
     * to keep track of current latitude
     */
    private var currentLat = 0.0

    /**
     * to keep track of current longitude
     */
    private var currentLong = 0.0

    private lateinit var mMap: GoogleMap

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false

        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(), R.raw.style_json))

        // prepare the callback to request location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val locationRequest = LocationRequest.create().apply {
            interval = 1 * 60 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                currentLat = locationResult.lastLocation.latitude
                currentLong = locationResult.lastLocation.longitude

                val currentTile: Long = getTileNumber(currentLat, currentLong)

                if (previousTile != currentTile) {
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(currentLat, currentLong), 15f
                        )
                    )

                    // make call to get data here

                    previousTile = currentTile
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())


    }


    private lateinit var binding: FragmentCheckNetworkBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_check_network, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibMyLocation.setOnClickListener {
            if(currentLat != 0.0 && currentLong != 0.0) {
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(currentLat, currentLong), 15f
                    )
                )
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onPause() {
        super.onPause()
        if(this::fusedLocationClient.isInitialized &&
            this::locationCallback.isInitialized)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * get the tile number based on the location
     *
     * @param latitude  location latitude
     * @param longitude location longitude
     * @return tile number
     */
    private fun getTileNumber(latitude: Double, longitude: Double): Long {
        val column =
            ((longitude - EGYPT_START_LONG) / (4 * h)).toLong()
        val row =
            ((latitude - EGYPT_START_LAT) / (4 * h)).toLong()
        return column + row * NO_OF_COLUMNS
    }
}