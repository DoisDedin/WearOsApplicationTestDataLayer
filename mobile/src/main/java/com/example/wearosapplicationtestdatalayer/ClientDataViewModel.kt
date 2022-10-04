package com.example.wearosapplicationtestdatalayer

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class ClientDataViewModel() : ViewModel(),
    DataClient.OnDataChangedListener,
    CapabilityClient.OnCapabilityChangedListener {

    var userNameLiveData = MutableLiveData<String>()
    var userHigherLiveData = MutableLiveData<String>()
    private var _userClicksLiveData = MutableLiveData<String>()
    val userClicksLiveData : LiveData<String>
        get() = _userClicksLiveData

    private lateinit var dataClient: DataClient

    var userNumberClicksLiveData = MutableLiveData<String>()

    fun startDataClient(context: Context) {
        dataClient = Wearable.getDataClient(context)
    }

    private val _capabilityNodes: MutableLiveData<Boolean> = MutableLiveData()
    val capabilityNodes: LiveData<Boolean>
        get() = _capabilityNodes

    override fun onDataChanged(dataInfo: DataEventBuffer) {
        dataInfo.map { event ->
            when (event.dataItem.uri.path) {
                NUMBER_CLICKS_INFO_PATH -> {
                    event.dataItem.also { item ->
                        DataMapItem.fromDataItem(item).dataMap.apply {
                            val clicks = getString(NUMBER_CLICKS_INFO_KEY, "-")
                            _userClicksLiveData.postValue(clicks)
                        }
                    }
                }
                else -> {}
            }
        }
    }


    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        _capabilityNodes.value = !capabilityInfo.nodes.isNullOrEmpty()
    }

    companion object {
        private const val NUMBER_CLICKS_INFO_PATH = "/number_clicks_info_path"
        private const val NUMBER_CLICKS_INFO_KEY = "number_clicks_info_key"
        private const val START_ACTIVITY_IN_CELL = "start_activity_in_cell"
        private const val USER_INFO_PATH = "/user_info_path"
        private const val NAME_USER_INFO_KEY = "user_info_name_key"
        private const val HEIGHT_USER_INFO_KEY = "user_info_height_key"
        private const val WEIGHT_USER_INFO_KEY = "user_info_weight_key"
        private const val TAG = "ClientDataViewModelCell"
    }

    fun sendInfoToWear(name: String, heightUser: String, weightUser: String) {
        runBlocking {
            try {
                val request = PutDataMapRequest.create(USER_INFO_PATH).apply {
                    dataMap.apply {
                        putString(NAME_USER_INFO_KEY, name)
                        putString(HEIGHT_USER_INFO_KEY, heightUser)
                        putString(WEIGHT_USER_INFO_KEY, weightUser)
                    }
                }
                    .asPutDataRequest()
                    .setUrgent()

                val result = dataClient.putDataItem(request).await()

                Log.d(TAG, "DataItem saved: $result")
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.d(TAG, "Saving DataItem failed: $exception")
            }
        }
    }
}