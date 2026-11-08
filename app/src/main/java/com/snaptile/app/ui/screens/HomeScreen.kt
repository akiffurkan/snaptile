package com.snaptile.app.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.snaptile.app.R
import com.snaptile.app.accessibility.ScreenshotServiceHolder
import com.snaptile.app.accessibility.ScreenshotServiceHolder.ScreenshotResult
import com.snaptile.app.ui.components.FloatingButtonCard
import com.snaptile.app.ui.components.PrivacyCard
import com.snaptile.app.ui.components.SetupInstructionsCard
import com.snaptile.app.ui.components.StatusCard
import com.snaptile.app.ui.components.TestScreenshotCard
import com.snaptile.app.utils.AccessibilityUtils
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    isAccessibilityActive: Boolean,
    isSystemSupported: Boolean,
    isFloatingEnabled: Boolean,
    onEnableAccessibilityClick: () -> Unit,
    onFloatingToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val onTestScreenshotClick: () -> Unit = {
        if (!isAccessibilityActive) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.msg_service_disabled)
                )
            }
        } else {
            val result = ScreenshotServiceHolder.takeScreenshotDirect()
            val message = when (result) {
                ScreenshotResult.SUCCESS -> context.getString(R.string.msg_screenshot_success)
                ScreenshotResult.SERVICE_NOT_CONNECTED -> context.getString(R.string.msg_service_disabled)
                ScreenshotResult.ACTION_FAILED -> context.getString(R.string.msg_screenshot_failed)
                ScreenshotResult.DEBOUNCED -> context.getString(R.string.msg_debounce_warning)
            }
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    val onDirectAddTileClick: (() -> Unit)? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        {
            AccessibilityUtils.requestAddTile(
                context = context,
                executor = ContextCompat.getMainExecutor(context)
            ) { resultCode ->
                val text = if (resultCode == 0) {
                    context.getString(R.string.msg_tile_request_added)
                } else {
                    context.getString(R.string.qs_setup_title)
                }
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section with new camera icon
            HeaderSection()

            // Status Card
            StatusCard(
                isAccessibilityActive = isAccessibilityActive,
                isSystemSupported = isSystemSupported
            )

            // Test & Enable Accessibility Action Card
            TestScreenshotCard(
                isAccessibilityActive = isAccessibilityActive,
                onEnableAccessibilityClick = onEnableAccessibilityClick,
                onTestScreenshotClick = onTestScreenshotClick
            )

            // Floating Draggable Screenshot Button Control
            FloatingButtonCard(
                isFloatingEnabled = isFloatingEnabled,
                onToggle = onFloatingToggle
            )

            // Quick Settings Setup Instructions
            SetupInstructionsCard(
                onDirectAddClick = onDirectAddTileClick
            )

            // Privacy & Security Card
            PrivacyCard()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_screenshot_tile),
            contentDescription = null,
            modifier = Modifier.size(52.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.app_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
