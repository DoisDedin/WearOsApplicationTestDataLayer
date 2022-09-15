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
    var userClicksLiveData = MutableLiveData<String>()

    private lateinit var dataClient: DataClient
    //by lazy {  }

    fun startDataClient(context: Context) {
        dataClient = Wearable.getDataClient(context)
    }

    private val _capabilityNodes: MutableLiveData<Boolean> = MutableLiveData()
    val capabilityNodes: LiveData<Boolean>
        get() = _capabilityNodes

    override fun onDataChanged(dataInfo: DataEventBuffer) {
        dataInfo.map {
            when (it.type) {
                1 -> {} //fazer algo com o nome do usuario
                2 -> {}//fazer algo com a altura do usaurio
                3 -> {}// fazer algo com o numero de cliks do usuario
                else -> {}//fazer nada
            }
        }
    }


    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        _capabilityNodes.value = !capabilityInfo.nodes.isNullOrEmpty()
    }

    companion object {
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