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
        dataInfo.forEach {
            val uri = it.dataItem.uri
            when (uri.path) {
                COUNT_CLICKS_PATH -> {
                    scope.launch {
                        userClicksLiveData.value = uri.toString()
                    }
                }
                else -> {}//fazer nada
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
        const val COUNT_CLICKS_PATH = "/count_clicks"
        private const val COUNT_KEY = "count_clicks"

        private const val TAG = "DataLayerService"
    }


}