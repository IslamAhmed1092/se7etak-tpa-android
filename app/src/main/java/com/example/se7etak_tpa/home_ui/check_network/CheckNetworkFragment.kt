package com.example.se7etak_tpa.home_ui.check_network

import android.annotation.SuppressLint

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.data.MapFilter
import com.example.se7etak_tpa.data.Provider
import com.example.se7etak_tpa.databinding.BottomSheetProviderBinding
import com.example.se7etak_tpa.databinding.FragmentCheckNetworkBinding
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog

class CheckNetworkFragment : Fragment() {

    private val viewModel: CheckNetworkViewModel by viewModels()

    /**
     * to keep track of tile changes
     */
    private var previousTile: Long = -1


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var mMap: GoogleMap

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false

        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(), R.raw.style_json
            )
        )

        // prepare the callback to request location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val locationRequest = LocationRequest.create().apply {
            interval = 1 * 60 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                viewModel.setCurrentLocation(locationResult.lastLocation)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

/*        viewModel.currentLocation.observe(viewLifecycleOwner) {
            if (it != null){
//                Toast.makeText(context, "current location changed", Toast.LENGTH_SHORT).show()
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        it, 15f
                    )
                )
            }

        }*/

        viewModel.currentTile.observe(viewLifecycleOwner) {
            if (previousTile != it) {
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom (
                        viewModel.currentLocation.value!!, 15f
                    )
                )

                viewModel.updateProviders()
            }
            previousTile = it
        }

        viewModel.providersMap.observe(viewLifecycleOwner) {
            mMap.clear()
            for((type, list) in it) {
                list.forEach { provider ->
                    val options = MarkerOptions()
                        .position(LatLng(provider.latitude, provider.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(
                            CheckNetworkViewModel.typesColors[type]!!)
                        )
                    val marker = mMap.addMarker(options)
                    marker?.apply{
                        tag = provider
                    }
                    provider.marker = marker
                }

            }
        }

        mMap.setOnMarkerClickListener { marker ->
            (marker.tag as Provider).let {
                val bottomSheet = BottomSheetDialog(requireContext())
                val bindingSheet = DataBindingUtil.inflate<BottomSheetProviderBinding>(
                    layoutInflater,
                    R.layout.bottom_sheet_provider,
                    null,
                    false
                )
                bottomSheet.setContentView(bindingSheet.root)
                bindingSheet.provider = it
                bottomSheet.show()
            }
            false
        }

    }


    private lateinit var binding: FragmentCheckNetworkBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_check_network, container, false)

        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        WindowInsetsControllerCompat(
            activity?.window!!,
            activity?.window!!.decorView
        ).isAppearanceLightStatusBars = true

        binding.rvFilters.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, true)

        binding.rvFilters.itemAnimator = DefaultItemAnimator()
        binding.rvFilters.addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.horizontal_spacing)))
        binding.rvFilters.adapter = FiltersListAdapter(requireContext()) {
            viewModel.showHideMarkers(it.name, it.isEnabled)
        }

        binding.rvFilters.setHasFixedSize(true)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        WindowInsetsControllerCompat(
            activity?.window!!,
            activity?.window!!.decorView
        ).isAppearanceLightStatusBars = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibMyLocation.setOnClickListener {
            if (viewModel.currentLocation.value != null) {
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        viewModel.currentLocation.value!!, 15f
                    )
                )
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onPause() {
        super.onPause()

        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        WindowInsetsControllerCompat(
            activity?.window!!,
            activity?.window!!.decorView
        ).isAppearanceLightStatusBars = false
    }


}