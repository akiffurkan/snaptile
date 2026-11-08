package com.snaptile.app.utils

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.snaptile.app.R
import com.snaptile.app.accessibility.ScreenshotAccessibilityService
import com.snaptile.app.accessibility.ScreenshotServiceHolder
import com.snaptile.app.tiles.ScreenshotTileService
import java.util.concurrent.Executor

object AccessibilityUtils {

    /**
     * Checks if the device meets minimum requirement (Android 9 / API 28+).
     */
    fun isSystemSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    /**
     * Checks whether ScreenshotAccessibilityService is currently enabled in system settings
     * or has an active connected instance.
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        if (ScreenshotServiceHolder.isServiceConnected()) {
            return true
        }

        val expectedComponentName = ComponentName(context, ScreenshotAccessibilityService::class.java).flattenToString()
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)

        while (colonSplitter.hasNext()) {
            val componentName = colonSplitter.next()
            if (componentName.equals(expectedComponentName, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    /**
     * Opens Android's system Accessibility settings page.
     */
    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    /**
     * Direct Quick Settings tile request for Android 13+ (API 33+).
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestAddTile(context: Context, executor: Executor, onResult: (Int) -> Unit) {
        val statusBarManager = context.getSystemService(StatusBarManager::class.java) ?: return
        val componentName = ComponentName(context, ScreenshotTileService::class.java)
        val icon = Icon.createWithResource(context, R.drawable.ic_screenshot_tile)

        statusBarManager.requestAddTileService(
            componentName,
            context.getString(R.string.tile_label),
            icon,
            executor
        ) { resultCode ->
            onResult(resultCode)
        }
    }
}
