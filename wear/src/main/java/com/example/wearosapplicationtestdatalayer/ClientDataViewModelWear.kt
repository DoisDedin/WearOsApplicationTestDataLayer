package com.example.wearosapplicationtestdatalayer

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ClientDataViewModelWear(application: Application) : AndroidViewModel(application),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    var userNameLiveData = MutableLiveData<String>()
    var userHigherLiveData = MutableLiveData<String>()
    var userWeightLiveData = MutableLiveData<String>()
    private var _userClicksNumbers = MutableLiveData<Int>()
    val userClicksNumbers: LiveData<Int>
        get() = _userClicksNumbers

    private lateinit var dataClient: DataClient

    fun startDataClient(context: Context) {
        dataClient = Wearable.getDataClient(context)
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    override fun onDataChanged(dataInfo: DataEventBuffer) {
        dataInfo.forEach { event ->
            when (event.dataItem.uri.path) {
                USER_INFO_PATH -> {
                    event.dataItem.also { item ->
                        scope.launch {
                            DataMapItem.fromDataItem(item).dataMap.apply {
                                userWeightLiveData.value =
                                    getString(WEIGHT_USER_INFO_KEY, "-").toString()
                                userNameLiveData.value = getString(NAME_USER_INFO_KEY, "-")
                                userHigherLiveData.value =
                                    getString(HEIGHT_USER_INFO_KEY, "-").toString()
                            }
                        }
                    }
                }
            }
        }
    }

    fun sendNumberClicksToCell(numberClicks: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = PutDataMapRequest.create(NUMBER_CLICKS_INFO_PATH).apply {
                    dataMap.apply {
                        putString(NUMBER_CLICKS_INFO_KEY, numberClicks)
                    }
                }
                    .asPutDataRequest()
                    .setUrgent()

                val result = dataClient.putDataItem(request).await()
                Log.d(TAG, "DataItemClicks saved: $result")
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.d(TAG, "Saving DataItemClicks failed: $exception")
            }
        }
    }

    fun addOneClick() {
        var issnt = userClicksNumbers.value ?: 0
        issnt++
        _userClicksNumbers.postValue(issnt)
    }

    override fun onMessageReceived(p0: MessageEvent) {
        Log.d(TAG, "Receive")
    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {
        //metodo responsavel por escutar se o relogio esta conectado no celular ou não
    }

    companion object {
        private const val USER_INFO_PATH = "/user_info_path"
        private const val NUMBER_CLICKS_INFO_PATH = "/number_clicks_info_path"
        private const val NAME_USER_INFO_KEY = "user_info_name_key"
        private const val NUMBER_CLICKS_INFO_KEY = "number_clicks_info_key"
        private const val HEIGHT_USER_INFO_KEY = "user_info_height_key"
        private const val WEIGHT_USER_INFO_KEY = "user_info_weight_key"
        private const val TAG = "ClientDataViewModelWear"
    }


}