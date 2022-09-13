package com.example.wearosapplicationtestdatalayer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wearosapplicationtestdatalayer.databinding.ActivityMainWaerBinding
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivityWear : ComponentActivity() {

    private lateinit var binding: ActivityMainWaerBinding

    private val clientDataViewModelWear by viewModels<ClientDataViewModelWear>()

    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }
    private val nodeClient by lazy { Wearable.getNodeClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainWaerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observerDataChanged()
        setListeners()
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


    private fun setListeners() {
        binding.buttonAddClicks.setOnClickListener {
            //  sendClickTomMobile()
        }
        binding.buttonStartActivityInCell.setOnClickListener {
            sendClickTomMobile()
        }
    }


    private fun sendClickTomMobile() {

        lifecycleScope.launch {
            try {
                val nodes = nodeClient.connectedNodes.await()
                // Send a message to all nodes in parallel
                nodes.map { node ->
                    async {
                        messageClient.sendMessage(node.id, START_ACTIVITY_IN_CELL, byteArrayOf())
                            .await()
                    }
                }.awaitAll()
                Log.d(TAG, "Starting activity in cell requests sent successfully")
            } catch (cancellationException: CancellationException) {
                Log.d(TAG, "Starting activity in cellfailed: $cancellationException")
            } catch (exception: Exception) {
                Log.d(TAG, "Starting activity in cellfailed: $exception")
            }
        }
    }


    companion object {
        private const val START_ACTIVITY_IN_CELL = "start_activity_in_cell"
        private const val TAG = "MainActivityWear"
    }

}