package com.example.wearosapplicationtestdatalayer

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivityWear : ComponentActivity() {

    private lateinit var binding: ActivityMainWaerBinding

    private val clientDataViewModelWear by viewModels<ClientDataViewModelWear>()

    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

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
            sendClickTomMobile()
        }
    }


    private fun sendClickTomMobile() {
        lifecycleScope.launch {
            try {
                val nodes = getCapabilitiesForReachableNodes()
                    .filterValues { "mobile" in it && "camera" in it }.keys
                displayNodes(nodes)
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.d(TAG, "Querying nodes failed: $exception")
            }
        }
    }

    private suspend fun getCapabilitiesForReachableNodes(): Map<Node, Set<String>> =
        capabilityClient.getAllCapabilities(CapabilityClient.FILTER_REACHABLE)
            .await()
            // Pair the list of all reachable nodes with their capabilities
            .flatMap { (capability, capabilityInfo) ->
                capabilityInfo.nodes.map { it to capability }
            }
            // Group the pairs by the nodes
            .groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )
            // Transform the capability list for each node into a set
            .mapValues { it.value.toSet() }


    private fun displayNodes(nodes: Set<Node>) {
        val message = if (nodes.isEmpty()) {
            getString(R.string.no_device)
        } else {
            getString(R.string.connected_nodes, nodes.joinToString(", ") { it.displayName })
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainActivityWear"
    }

}