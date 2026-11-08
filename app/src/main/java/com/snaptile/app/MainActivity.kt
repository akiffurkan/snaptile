package com.snaptile.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.snaptile.app.accessibility.ScreenshotServiceHolder
import com.snaptile.app.floating.FloatingButtonService
import com.snaptile.app.ui.screens.HomeScreen
import com.snaptile.app.ui.theme.SnapTileTheme
import com.snaptile.app.utils.AccessibilityUtils

class MainActivity : ComponentActivity() {

    private var isSettingsEnabled by mutableStateOf(false)
    private var pendingEnableFloating = false

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(this)) {
            FloatingButtonService.start(this)
        } else {
            Toast.makeText(
                this,
                getString(R.string.floating_permission_dialog_desc),
                Toast.LENGTH_LONG
            ).show()
        }
        pendingEnableFloating = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SnapTileTheme {
                val isServiceConnected by ScreenshotServiceHolder.isConnected.collectAsState()
                val isFloatingRunning by FloatingButtonService.isRunning.collectAsState()
                val isEffectivelyActive = isServiceConnected || isSettingsEnabled
                val isSystemSupported = AccessibilityUtils.isSystemSupported()

                HomeScreen(
                    isAccessibilityActive = isEffectivelyActive,
                    isSystemSupported = isSystemSupported,
                    isFloatingEnabled = isFloatingRunning,
                    onEnableAccessibilityClick = {
                        AccessibilityUtils.openAccessibilitySettings(this)
                    },
                    onFloatingToggle = { enable ->
                        if (enable) {
                            if (Settings.canDrawOverlays(this)) {
                                FloatingButtonService.start(this)
                            } else {
                                pendingEnableFloating = true
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:$packageName")
                                )
                                overlayPermissionLauncher.launch(intent)
                            }
                        } else {
                            FloatingButtonService.stop(this)
                        }
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Check system settings to see if user just enabled the service
        isSettingsEnabled = AccessibilityUtils.isAccessibilityServiceEnabled(this)

        // If user granted overlay permission in settings and returned
        if (pendingEnableFloating && Settings.canDrawOverlays(this)) {
            FloatingButtonService.start(this)
            pendingEnableFloating = false
        }
    }
}
