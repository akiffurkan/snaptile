package com.snaptile.app.accessibility

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Thread-safe singleton registry holding the active ScreenshotAccessibilityService reference.
 * Provides debounce protection and safe execution of screenshot requests.
 */
object ScreenshotServiceHolder {

    enum class ScreenshotResult {
        SUCCESS,
        SERVICE_NOT_CONNECTED,
        ACTION_FAILED,
        DEBOUNCED
    }

    private const val DEBOUNCE_INTERVAL_MS = 600L
    private const val SHADE_COLLAPSE_DELAY_MS = 350L

    private val serviceReference = AtomicReference<ScreenshotAccessibilityService?>(null)
    private val lastScreenshotTimestamp = AtomicLong(0L)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    fun register(service: ScreenshotAccessibilityService) {
        serviceReference.set(service)
        _isConnected.value = true
    }

    fun unregister(service: ScreenshotAccessibilityService) {
        if (serviceReference.compareAndSet(service, null)) {
            _isConnected.value = false
        }
    }

    fun isServiceConnected(): Boolean {
        return serviceReference.get() != null
    }

    /**
     * Directly executes the native screenshot global action on the active accessibility service.
     */
    fun takeScreenshotDirect(): ScreenshotResult {
        val now = SystemClock.uptimeMillis()
        val lastTime = lastScreenshotTimestamp.get()
        if (now - lastTime < DEBOUNCE_INTERVAL_MS) {
            return ScreenshotResult.DEBOUNCED
        }

        val service = serviceReference.get() ?: return ScreenshotResult.SERVICE_NOT_CONNECTED

        if (!lastScreenshotTimestamp.compareAndSet(lastTime, now)) {
            return ScreenshotResult.DEBOUNCED
        }

        val accepted = service.performScreenshotAction()
        return if (accepted) ScreenshotResult.SUCCESS else ScreenshotResult.ACTION_FAILED
    }

    /**
     * Executes screenshot with a brief delay, allowing the Quick Settings shade to collapse
     * so that the screenshot captures the user's active screen rather than the shade.
     */
    fun takeScreenshotFromTile(onResult: (ScreenshotResult) -> Unit = {}) {
        val now = SystemClock.uptimeMillis()
        val lastTime = lastScreenshotTimestamp.get()
        if (now - lastTime < DEBOUNCE_INTERVAL_MS) {
            onResult(ScreenshotResult.DEBOUNCED)
            return
        }

        val service = serviceReference.get()
        if (service == null) {
            onResult(ScreenshotResult.SERVICE_NOT_CONNECTED)
            return
        }

        if (!lastScreenshotTimestamp.compareAndSet(lastTime, now)) {
            onResult(ScreenshotResult.DEBOUNCED)
            return
        }

        // Collapse notification shade first so screen underneath is exposed
        service.dismissNotificationShade()

        // Wait for shade collapse animation before capturing
        mainHandler.postDelayed({
            val activeService = serviceReference.get()
            if (activeService == null) {
                onResult(ScreenshotResult.SERVICE_NOT_CONNECTED)
                return@postDelayed
            }
            val accepted = activeService.performScreenshotAction()
            val result = if (accepted) ScreenshotResult.SUCCESS else ScreenshotResult.ACTION_FAILED
            onResult(result)
        }, SHADE_COLLAPSE_DELAY_MS)
    }
}
