package bismay.exe.fivegtile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import kotlin.concurrent.thread

class FiveGTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        refreshTileState()
    }

    override fun onClick() {
        super.onClick()
        qsTile.state = Tile.STATE_UNAVAILABLE
        qsTile.label = "5G…"
        qsTile.updateTile()

        thread {
            val subId = TelephonyToggles.getActiveDataSubId(this)
            val ok = if (subId != -1) TelephonyToggles.toggleNr(subId) else false

            runOnUiThread {
                if (!ok) {
                    Toast.makeText(this, "Failed: root/cmd not available", Toast.LENGTH_LONG).show()
                }
                refreshTileState()
            }
        }
    }

    private fun refreshTileState() {
        thread {
            val subId = TelephonyToggles.getActiveDataSubId(this)
            val isOn = if (subId != -1) TelephonyToggles.isNrAllowed(subId) else false

            runOnUiThread {
                qsTile.label = if (isOn) "5G On" else "5G Off"
                qsTile.state = if (isOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                qsTile.updateTile()
            }
        }
    }
}
