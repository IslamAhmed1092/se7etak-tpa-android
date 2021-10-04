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
            Provider(id = "1", name = "د/ احمد الشافعى", type = "اسنان", latitude = 29.9729578676011, longitude = 31.3152266681493),
            Provider(id = "2", name = "المركز المصري الأول لطب الأسنان", type = "اسنان", latitude = 29.9681761285931, longitude = 31.2545313736717),
            Provider(id = "3", name = "د/ هشام أحمد عيسى - ماستر دينتال كلينيك", type = "اسنان", latitude = 30.0544342250867, longitude = 31.1943694156769),
            Provider(id = "4", name = "سي &كو للبصريات", type = "مركز بصريات", latitude = 29.9830660945359, longitude = 31.3161109387816),
            Provider(id = "5", name = "مركز جولى اوبتكس", type = "مركز بصريات", latitude = 29.9761626555958, longitude = 31.2823747104774),
            Provider(id = "6", name = "صيدلية د/ محمود منصور (اكرم صلاح)", type = "صيدليات", latitude = 29.9781372449795, longitude = 31.2765012386507),
            Provider(id = "7", name = "د/ محمد سعد زغلول", type = "عيادات", latitude = 29.959795772258, longitude = 31.2550591258209),
            Provider(id = "8", name = "د/شيرين أحمد خليل", type = "عيادات", latitude = 29.9605111860232, longitude = 31.25408950803),
            Provider(id = "9", name = "عيادات داوى", type = "مجمع عيادات", latitude = 29.9612238617506, longitude = 31.2881135290003),
            Provider(id = "10", name = "مراكز د/ حسام منصور الطبية", type = "مجمع عيادات", latitude = 29.9588754580614, longitude = 31.257878410477),
            Provider(id = "11", name = "مستشفى المجمع الطبى للقوات المسلحة", type = "مستشفيات", latitude = 29.9673248392669, longitude = 31.2421771393131,
            city = "المعادي", governorate = "القاهرة", mainSpeciality = "جميع التخصصات الطبية", secondarySpeciality = "جميع التخصصات الطبية", address = "كورنيش المعادي",
                phoneNumber1 = "0225256289", phoneNumber2 = "01203925210", phoneNumber3 = "01099926026", hotline = "19668"),
            Provider(id = "12", name = "مستشفي السلام الدولي", type = "مستشفيات", latitude = 29.984954861938, longitude = 31.2301857123296),
            Provider(id = "13", name = "مستشفي النيل بدراوي ", type = "مستشفيات", latitude = 29.9828657102764, longitude = 31.2312070250417),
        )

        responseList.let { list ->
            _providersMap.value = list.groupBy { it.type }
        }
    }

    fun showHideMarkers(type: String, visible: Boolean) {
        _providersMap.value?.let{
            val list = it[type]
            list?.forEach { provider -> provider.marker?.isVisible = visible }
        }
    }


    companion object {
        val typesColors: Map<String, Float> = mapOf(
            "مستشفيات" to BitmapDescriptorFactory.HUE_ROSE,
            "صيدليات" to BitmapDescriptorFactory.HUE_MAGENTA,
            "عيادات" to BitmapDescriptorFactory.HUE_VIOLET,
            "مجمع عيادات" to BitmapDescriptorFactory.HUE_BLUE,
            "اسنان" to BitmapDescriptorFactory.HUE_AZURE,
            "معامل" to BitmapDescriptorFactory.HUE_CYAN,
            "مراكز متخصصه" to BitmapDescriptorFactory.HUE_GREEN,
            "مراكز علاج طبيعي" to BitmapDescriptorFactory.HUE_YELLOW,
            "مراكز اشعة" to BitmapDescriptorFactory.HUE_ORANGE,
            "مركز بصريات" to BitmapDescriptorFactory.HUE_RED
        )
    }

}