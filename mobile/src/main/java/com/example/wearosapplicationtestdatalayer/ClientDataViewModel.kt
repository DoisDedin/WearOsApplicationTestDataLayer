package com.example.wearosapplicationtestdatalayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.*

class ClientDataViewModel : ViewModel(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    var userNameLiveData = MutableLiveData<String>()
    var userHigherLiveData = MutableLiveData<String>()
    var userClicksLiveData = MutableLiveData<String>()


    override fun onDataChanged(dataInfo: DataEventBuffer) {
        dataInfo.map {
            when(it.type){
                1 ->{} //fazer algo com o nome do usuario
                2 -> {}//fazer algo com a altura do usaurio
                3 -> {}// fazer algo com o numero de cliks do usuario
                else -> {}//fazer nada
            }
        }
    }

    override fun onMessageReceived(p0: MessageEvent) {
        //verificar oque fazer com isso
    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {
        //metodo responsavel por escutar se o relogio esta conectado no celular ou não
    }
}