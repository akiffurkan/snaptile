package com.snaptile.app.tiles

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.snaptile.app.MainActivity
import com.snaptile.app.R
import com.snaptile.app.accessibility.ScreenshotServiceHolder

/**
 * Quick Settings TileService that exposes the "Ekran Görüntüsü" button in Android's QS panel.
 * When tapped:
 * - If accessibility service is active: collapses shade and captures native screenshot.
 * - If accessibility service is inactive: launches MainActivity to display setup instructions.
 */
class ScreenshotTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()

        if (!ScreenshotServiceHolder.isServiceConnected()) {
            // Guide user to the app to enable accessibility service
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Android 14+ PendingIntent approach if required, or startActivityAndCollapse
                startActivityAndCollapse(intent)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(intent)
            }
            updateTileState()
            return
        }

        // Service is active, perform native screenshot with shade collapse
        ScreenshotServiceHolder.takeScreenshotFromTile { result ->
            // Keep tile in ready state
            updateTileState()
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val isReady = ScreenshotServiceHolder.isServiceConnected()

        tile.label = getString(R.string.tile_label)
        tile.icon = Icon.createWithResource(this, R.drawable.ic_screenshot_tile)

        // Ready: STATE_ACTIVE (illuminated). Disabled: STATE_INACTIVE (available to tap and open setup).
        tile.state = if (isReady) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = getString(if (isReady) R.string.tile_subtitle_ready else R.string.tile_subtitle_setup)
        }

        tile.updateTile()
    }
}
