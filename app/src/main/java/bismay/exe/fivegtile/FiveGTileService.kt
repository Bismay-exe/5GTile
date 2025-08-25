package bismay.exe.fivegtile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.telephony.TelephonyManager
import android.os.Handler
import android.os.Looper
import android.util.Log

class FiveGTileService : TileService() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var telephonyManager: TelephonyManager

    override fun onStartListening() {
        super.onStartListening()
        telephonyManager = getSystemService(TelephonyManager::class.java)

        updateTile()
    }

    override fun onClick() {
        super.onClick()
        Log.d("FiveGTileService", "Tile clicked")

        val subId = TelephonyToggles.getActiveDataSubId(this)
        if (subId == -1) {
            Log.e("FiveGTileService", "No active subscription found")
            return
        }

        // Toggle 5G using TelephonyToggles
        val success = TelephonyToggles.toggleNr(subId)
        Log.d("FiveGTileService", "Toggle result: $success")

        updateTile()
    }

    private fun updateTile() {
        val subId = TelephonyToggles.getActiveDataSubId(this)
        val isNrAllowed = if (subId != -1) TelephonyToggles.isNrAllowed(subId) else false

        mainHandler.post {
            qsTile.state = if (isNrAllowed) {
                Tile.STATE_ACTIVE
            } else {
                Tile.STATE_INACTIVE
            }
            qsTile.updateTile()
        }
    }
}
