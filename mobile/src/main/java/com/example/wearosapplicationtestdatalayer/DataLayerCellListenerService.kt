package com.example.wearosapplicationtestdatalayer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class DataLayerCellListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        when (messageEvent.path) {
            START_ACTIVITY_IN_CELL -> {
                try {
                    application.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("vnd.youtube:o5zbLCjoc2Q")
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } catch (ex: ActivityNotFoundException) {
                    application.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=o5zbLCjoc2Q")
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
        }
    }


    companion object {
        private const val START_ACTIVITY_IN_CELL = "start_activity_in_cell"
    }
}