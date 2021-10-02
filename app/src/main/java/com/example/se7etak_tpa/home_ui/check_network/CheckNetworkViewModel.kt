package com.example.se7etak_tpa.home_ui.check_network

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.StatusObject
import com.example.se7etak_tpa.TAG
import com.example.se7etak_tpa.data.Provider
import com.example.se7etak_tpa.network.Api
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckNetworkViewModel : ViewModel() {

    private val EGYPT_START_LAT = 22
    private val EGYPT_START_LONG = 24
    private val EGYPT_END_LAT = 32
    private val EGYPT_END_LONG = 37
    private val h = 0.01
    private val NO_OF_ROWS = ((EGYPT_END_LAT - EGYPT_START_LAT) / (4 * h)).toInt() + 1
    private val NO_OF_COLUMNS = ((EGYPT_END_LONG - EGYPT_START_LONG) / (4 * h)).toInt() + 1

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng> get() = _currentLocation

    private val _currentTile = MutableLiveData<Long>()
    val currentTile: LiveData<Long> get() = _currentTile

    private val _providersMap = MutableLiveData<Map<String, List<Provider>>>()
    val providersMap: LiveData<Map<String, List<Provider>>> get() = _providersMap

    fun setCurrentLocation(location: Location) {
        _currentLocation.value = LatLng(location.latitude, location.longitude)
        updateTileNumber()
    }

    private fun updateTileNumber() {
        val column =
            ((_currentLocation.value?.longitude?.minus(EGYPT_START_LONG))?.div((4 * h)))?.toLong()
        val row =
            ((_currentLocation.value?.latitude?.minus(EGYPT_START_LAT))?.div((4 * h)))?.toLong()

        _currentTile.value = row?.times(NO_OF_COLUMNS)?.let { column?.plus(it) }
    }

    fun updateProviders() {
        /*val callResponse = Api.retrofitService.getProviders(_currentTile.value!!)

        callResponse.enqueue(object : Callback<List<Provider>> {
            override fun onResponse(
                call: Call<List<Provider>>,
                response: Response<List<Provider>>
            ) {
                if(response.code() == 200) {
                    val responseList = response.body()
                    responseList?.let { list ->
                        _providersMap.value = list.groupBy { it.type }
                    }
                    Log.i(TAG, "onResponse ${response.code()}: ${response.message()}" )
                } else {
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<List<Provider>>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }


        })*/

        val responseList = listOf(
            Provider("1", "عيادة سمارت للاسنان", "اسنان", "رئيسي","طب الأسنان وجراحة الفم", "طب الأسنان وجراحة الفم",
                "القاهرة ", "الزيتون", "287 أ برج الطاهرة شارع ترعة الجبل - حدائق الزيتون",
                "0222842532", "01019170563", "", "", 29.9664177816521,31.2748687821907
            )

            ,Provider("2", "المركز المصري الأول لطب الأسنان", "اسنان","", "طب الأسنان وجراحة الفم", "طب الأسنان وجراحة الفم",
            "القاهرة ", "الزيتون", "287 أ برج الطاهرة شارع ترعة الجبل - حدائق الزيتون",
            "0222842532", "01019170563", "", "", 29.9681761285931,31.2545313736717
            )

            ,Provider("3", "اى فاشون  للبصريات ", "مركز بصريات","", "طب الأسنان وجراحة الفم", "طب الأسنان وجراحة الفم",
                "القاهرة ", "الزيتون", "287 أ برج الطاهرة شارع ترعة الجبل - حدائق الزيتون",
                "0222842532", "01019170563", "", "", 29.9830660945359,31.3161109387816
            )

            ,Provider("4", "صيدلية د/ محمود منصور (اكرم صلاح)", "صيدليات","", "طب الأسنان وجراحة الفم", "طب الأسنان وجراحة الفم",
                "القاهرة ", "الزيتون", "287 أ برج الطاهرة شارع ترعة الجبل - حدائق الزيتون",
                "0222842532", "01019170563", "", "", 29.9781372449795,31.2765012386507
            )

            ,Provider("5", "د/ محمد سعد زغلول", "عيادات", "","طب الأسنان وجراحة الفم", "طب الأسنان وجراحة الفم",
                "القاهرة ", "الزيتون", "287 أ برج الطاهرة شارع ترعة الجبل - حدائق الزيتون",
                "0222842532", "01019170563", "", "", 29.959795772258,31.2550591258209
            )

        )
        responseList.let { list ->
            _providersMap.value = list.groupBy { it.type }
        }
    }

    companion object {
        val typesColors: Map<String, Float> = mapOf(
            "اسنان" to BitmapDescriptorFactory.HUE_AZURE,
            "مركز بصريات" to BitmapDescriptorFactory.HUE_GREEN,
            "صيدليات" to BitmapDescriptorFactory.HUE_CYAN,
            "عيادات" to BitmapDescriptorFactory.HUE_BLUE,
            "مجمع عيادات" to BitmapDescriptorFactory.HUE_MAGENTA,
            "مراكز اشعة" to BitmapDescriptorFactory.HUE_ORANGE,
            "مراكز علاج طبيعي" to BitmapDescriptorFactory.HUE_RED,
            "مراكز متخصصه" to BitmapDescriptorFactory.HUE_ROSE,
            "مستشفيات" to BitmapDescriptorFactory.HUE_VIOLET,
            "معامل" to BitmapDescriptorFactory.HUE_YELLOW
        )
    }
}