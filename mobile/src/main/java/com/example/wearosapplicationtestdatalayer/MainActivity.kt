package com.example.wearosapplicationtestdatalayer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.wearosapplicationtestdatalayer.databinding.ActivityMainBinding
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class MainActivity : ComponentActivity() {


    private lateinit var binding: ActivityMainBinding

    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }
    private val nodeClient by lazy { Wearable.getNodeClient(this) }


    private val countClicks: Int = 0

    private lateinit var clientDataViewModel: ClientDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //activity creates the instance of MapViewModel
        clientDataViewModel = ViewModelProvider(this).get(ClientDataViewModel::class.java)
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(clientDataViewModel)
        messageClient.addListener(clientDataViewModel)
        capabilityClient.addListener(
            clientDataViewModel,
            Uri.parse("wear://"),
            CapabilityClient.FILTER_REACHABLE
        )
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(clientDataViewModel)
        messageClient.removeListener(clientDataViewModel)
        capabilityClient.removeListener(clientDataViewModel)
    }

    private fun setListeners() {
        binding.buttonSendvalues.setOnClickListener {
            runBlocking {
                sendValues()
            }
        }

        binding.buttonStartwearactivity.setOnClickListener {
            runBlocking {

            }
        }
    }


    private suspend fun sendValues() {
        try {
            val request = PutDataMapRequest.create(COUNT_CLICKS_PATH).apply {
                dataMap.putInt(COUNT_KEY, countClicks)
            }.asPutDataRequest().setUrgent()

            val result = dataClient.putDataItem(request).await()

            Log.d(TAG, "DataItem saved: $result")
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (exception: Exception) {
            Log.d(TAG, "Saving DataItem failed: $exception")
        }
    }


    companion object {
        private const val TAG = "DataLayerService"
        private const val DATA_ITEM_RECEIVED_PATH = "/data-item-received"
        const val COUNT_CLICKS_PATH = "/count_clicks"
        private const val COUNT_KEY = "count_clicks"
    }


}