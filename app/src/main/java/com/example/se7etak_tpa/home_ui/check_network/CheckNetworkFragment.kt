package com.example.se7etak_tpa.home_ui.check_network

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.data.Provider
import com.example.se7etak_tpa.databinding.BottomSheetProviderBinding
import com.example.se7etak_tpa.databinding.FragmentCheckNetworkBinding
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class CheckNetworkFragment : Fragment() {

    private val viewModel: CheckNetworkViewModel by viewModels()

    private var previousTile: Long = -1

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var firebaseAnalytics: FirebaseAnalytics

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
                if(previousTile == -1L) {
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            viewModel.currentLocation.value!!, 15f
                        )
                    )
                }
                viewModel.pinnedLocationMarker.value?.remove()
                val options = MarkerOptions()
                    .position(viewModel.currentLocation.value!!)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_pin_blue))

                viewModel.setPinnedLocation(viewModel.currentLocation.value!!)
                viewModel.selectedLocationMarker.value = null
                val marker = mMap.addMarker(options)
                marker?.tag = "pinned"
                viewModel.pinnedLocationMarker.value = marker

                viewModel.updateProviders(viewModel.currentTile.value!!)
            }
            previousTile = it
        }

        binding.btnPin.setOnClickListener {
            viewModel.selectedLocationMarker.value?.let {

                viewModel.pinnedLocationMarker.value?.remove()
                val options = MarkerOptions()
                    .position(it.position)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_pin_blue))

                viewModel.setPinnedLocation(it.position)
                it.remove()
                viewModel.selectedLocationMarker.value = null
                val marker = mMap.addMarker(options)
                marker?.tag = "pinned"
                viewModel.pinnedLocationMarker.value = marker
                viewModel.updateProviders(viewModel.pinnedTile.value!!)
                binding.llPin.visibility = View.GONE
            }

        }

        binding.btnCancel.setOnClickListener {
            viewModel.selectedLocationMarker.value?.remove()
            viewModel.selectedLocationMarker.value = null
            binding.llPin.visibility = View.GONE
        }

        viewModel.providersMap.observe(viewLifecycleOwner) {
            mMap.clear()

            viewModel.selectedLocationMarker.value?.let {
                val options = MarkerOptions()
                    .position(it.position)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_pin_red))
                val marker = mMap.addMarker(options)
                marker?.tag = "selected"
                viewModel.selectedLocationMarker.value = marker
            }

            viewModel.pinnedLocationMarker.value?.let {
                val options = MarkerOptions()
                    .position(it.position)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_pin_blue))
                val marker = mMap.addMarker(options)
                marker?.tag = "pinned"
                viewModel.pinnedLocationMarker.value = marker
            }

            for((type, list) in it) {
                list.forEach { provider ->
                    val options = MarkerOptions()
                        .position(LatLng(provider.latitude, provider.longitude))
                        .icon(CheckNetworkViewModel.typesColors[type]?.let { it1 ->
                            BitmapDescriptorFactory.defaultMarker(
                                it1
                            )
                        }
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
            if(marker.tag?.toString() == "selected") {
                Toast.makeText(context, "click on pin location button to pin", Toast.LENGTH_SHORT).show()
            } else if (marker.tag?.toString() == "pinned"){
                Toast.makeText(context, "your currently pinned location", Toast.LENGTH_SHORT).show()
            } else {
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
            }
            false
        }

        mMap.setOnMapClickListener {
            viewModel.selectedLocationMarker.value?.remove()
            val options = MarkerOptions()
                .position(it)
                .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_pin_red))

            val marker = mMap.addMarker(options)
            marker?.tag = "selected"
            viewModel.selectedLocationMarker.value = marker
            binding.llPin.visibility = View.VISIBLE
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


        firebaseAnalytics = Firebase.analytics

        binding.rvFilters.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, true)


        binding.rvFilters.itemAnimator = DefaultItemAnimator()
        binding.rvFilters.addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.horizontal_spacing)))
        binding.rvFilters.adapter = FiltersListAdapter(viewModel.filtersList, viewLifecycleOwner) {
            if(it.name != "الكل") {
                firebaseAnalytics.logEvent("Filter Map") {
                    param("Provider Type", it.name)
                }
            }
            viewModel.showHideMarkers(it.name)
        }

        binding.rvFilters.setHasFixedSize(true)

        binding.btnTurnOn.setOnClickListener { checkPermission() }

        return binding.root
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


        // Checking if permission is not granted
        checkPermission()

    }

    private val permReqLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it) {
            binding.clGranted.visibility = View.VISIBLE
            binding.clNotGranted.visibility = View.GONE
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        } else {
            binding.clGranted.visibility = View.GONE
            binding.clNotGranted.visibility = View.VISIBLE
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permReqLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            binding.clGranted.visibility = View.VISIBLE
            binding.clNotGranted.visibility = View.GONE
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(callback)
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

}