package com.snaptile.app.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import android.view.accessibility.AccessibilityEvent
import com.snaptile.app.tiles.ScreenshotTileService

/**
 * Lightweight AccessibilityService whose sole purpose is to trigger Android's
 * native system screenshot global action on behalf of the Quick Settings tile.
 *
 * This service does not inspect window content, record screens, or log events.
 */
class ScreenshotAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        ScreenshotServiceHolder.register(this)
        requestTileStateRefresh()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        ScreenshotServiceHolder.unregister(this)
        requestTileStateRefresh()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        ScreenshotServiceHolder.unregister(this)
        requestTileStateRefresh()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No-op: SnapTile does not read, process, or collect any accessibility events.
    }

    override fun onInterrupt() {
        // Required by base class.
    }

    /**
     * Executes Android's native screenshot action via the Accessibility framework.
     * System manages screenshot preview, animation, and storage to Gallery.
     */
    fun performScreenshotAction(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
        } else {
            false
        }
    }

    /**
     * Closes the notification shade if supported so the underlying screen is captured cleanly.
     */
    fun dismissNotificationShade(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
        } else {
            false
        }
    }

    private fun requestTileStateRefresh() {
        try {
            TileService.requestListeningState(
                applicationContext,
                ComponentName(applicationContext, ScreenshotTileService::class.java)
            )
        } catch (_: Exception) {
            // Ignore any IPC errors if system tile service is not currently bound
        }
    }
}
