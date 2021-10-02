package com.example.se7etak_tpa.home_ui.check_network

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class CheckNetworkViewModel : ViewModel() {
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

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng> get() = _currentLocation

    private val _currentTile = MutableLiveData<Long>()
    val currentTile: LiveData<Long> get() = _currentTile

    fun setCurrentLocation(location: Location) {
        _currentLocation.value = LatLng(location.latitude, location.longitude)
        updateTileNumber()
    }


    /**
     * update the tile number based on the location
     */
    private fun updateTileNumber() {
        val column =
            ((_currentLocation.value?.longitude?.minus(EGYPT_START_LONG))?.div((4 * h)))?.toLong()
        val row =
            ((_currentLocation.value?.latitude?.minus(EGYPT_START_LAT))?.div((4 * h)))?.toLong()

        _currentTile.value = row?.times(NO_OF_COLUMNS)?.let { column?.plus(it) }
    }
}