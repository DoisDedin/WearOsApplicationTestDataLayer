package com.example.wearosapplicationtestdatalayer

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.wearosapplicationtestdatalayer.databinding.ActivityMainWaerBinding
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable

class MainActivityWear : ComponentActivity() {

    private lateinit var binding: ActivityMainWaerBinding


    private val clientDataViewModelWear by viewModels<ClientDataViewModelWear>()

    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityMainWaerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observerDataChanged()
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(clientDataViewModelWear)
        messageClient.addListener(clientDataViewModelWear)
        capabilityClient.addListener(
            clientDataViewModelWear,
            Uri.parse("wear://"),
            CapabilityClient.FILTER_REACHABLE
        )
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(clientDataViewModelWear)
        messageClient.removeListener(clientDataViewModelWear)
        capabilityClient.removeListener(clientDataViewModelWear)
    }


    private fun observerDataChanged() {
        clientDataViewModelWear.userClicksLiveData.observe(this) {
            binding.textviewNumeroclicks.text = it.orEmpty()
        }

        clientDataViewModelWear.userNameLiveData.observe(this) {
            binding.textviewNameuser.text = it.orEmpty()
        }

        clientDataViewModelWear.userHigherLiveData.observe(this) {
            binding.textviewAlturauser.text = it.orEmpty()
        }
    }

}