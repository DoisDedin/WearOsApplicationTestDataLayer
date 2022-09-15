package com.example.wearosapplicationtestdatalayer

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.wearable.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ClientDataViewModelWear(application: Application) : AndroidViewModel(application),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    var userNameLiveData = MutableLiveData<String>()
    var userHigherLiveData = MutableLiveData<String>()
    var userClicksLiveData = MutableLiveData<String>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    override fun onDataChanged(dataInfo: DataEventBuffer) {
        dataInfo.forEach { event ->
            when (event.dataItem.uri.path) {
                USER_INFO_PATH -> {
                    event.dataItem.also { item ->
                        scope.launch {
                            DataMapItem.fromDataItem(item).dataMap.apply {
                                userClicksLiveData.value =
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

    override fun onMessageReceived(p0: MessageEvent) {
        Log.d(TAG, "Receive")
    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {
        //metodo responsavel por escutar se o relogio esta conectado no celular ou não
    }

    companion object {
        private const val USER_INFO_PATH = "/user_info_path"
        private const val NAME_USER_INFO_KEY = "user_info_name_key"
        private const val HEIGHT_USER_INFO_KEY = "user_info_height_key"
        private const val WEIGHT_USER_INFO_KEY = "user_info_weight_key"
        private const val TAG = "ClientDataViewModelWear"
    }


}