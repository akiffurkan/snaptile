package com.snaptile.app.floating

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.snaptile.app.MainActivity
import com.snaptile.app.R
import com.snaptile.app.accessibility.ScreenshotServiceHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.hypot

class FloatingButtonService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "snaptile_floating_button_channel"
        private const val PREFS_NAME = "snaptile_prefs"
        const val PREF_FLOATING_ENABLED = "pref_floating_enabled"
        private const val PREF_X = "pref_floating_x"
        private const val PREF_Y = "pref_floating_y"

        const val ACTION_STOP = "com.snaptile.app.ACTION_STOP_FLOATING"

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        fun isEnabledInPrefs(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(PREF_FLOATING_ENABLED, false)
        }

        fun setEnabledInPrefs(context: Context, enabled: Boolean) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(PREF_FLOATING_ENABLED, enabled).apply()
        }

        fun start(context: Context) {
            if (!Settings.canDrawOverlays(context)) return
            setEnabledInPrefs(context, true)
            val intent = Intent(context, FloatingButtonService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            setEnabledInPrefs(context, false)
            val intent = Intent(context, FloatingButtonService::class.java)
            context.stopService(intent)
        }
    }

    private var windowManager: WindowManager? = null
    private var rootView: FrameLayout? = null
    private var hidePillView: LinearLayout? = null
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var prefs: SharedPreferences

    private val mainHandler = Handler(Looper.getMainLooper())
    private var longPressRunnable: Runnable? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        initFloatingView()
        _isRunning.value = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onDestroy() {
        removeFloatingView()
        _isRunning.value = false
        setEnabledInPrefs(this, false)
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.floating_notif_channel),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.floating_notif_desc)
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingOpenApp = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, FloatingButtonService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStop = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.floating_notif_title))
            .setContentText(getString(R.string.floating_notif_desc))
            .setSmallIcon(R.drawable.ic_screenshot_tile)
            .setContentIntent(pendingOpenApp)
            .addAction(0, getString(R.string.floating_hide_btn), pendingStop)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFloatingView() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density

        val savedX = prefs.getInt(PREF_X, (displayMetrics.widthPixels - 70 * density).toInt())
        val savedY = prefs.getInt(PREF_Y, (displayMetrics.heightPixels / 3))

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = savedX
            y = savedY
        }

        val container = FrameLayout(this).apply {
            clipChildren = false
            clipToPadding = false
        }

        // Horizontal container holding Camera Button + Hide Pill
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            clipChildren = false
            clipToPadding = false
        }

        // 1. Circular Camera Button
        val buttonSize = (54 * density).toInt()
        val cameraButton = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(buttonSize, buttonSize)

            // Circle background with subtle border and elevation
            val bgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#0D6EFD")) // Samsung / Material Blue
                setStroke((1.5f * density).toInt(), Color.parseColor("#80FFFFFF"))
            }
            background = bgDrawable
            elevation = 16f * density
        }

        val iconView = ImageView(this).apply {
            val iconPadding = (13 * density).toInt()
            setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
            setImageResource(R.drawable.ic_screenshot_tile)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        cameraButton.addView(iconView)

        // 2. Hide Pill ("✕ Gizle") that appears on long press
        val pill = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            visibility = View.GONE
            val padH = (12 * density).toInt()
            val padV = (8 * density).toInt()
            setPadding(padH, padV, padH, padV)

            val pillBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20 * density
                setColor(Color.parseColor("#CC1E1E1E"))
                setStroke((1 * density).toInt(), Color.parseColor("#80FFFFFF"))
            }
            background = pillBg
            elevation = 18f * density

            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = (8 * density).toInt()
            }
            layoutParams = lp
        }

        val pillText = TextView(this).apply {
            text = "✕ " + getString(R.string.floating_hide_btn)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        pill.addView(pillText)
        hidePillView = pill

        pill.setOnClickListener {
            Toast.makeText(this, getString(R.string.floating_hidden_toast), Toast.LENGTH_SHORT).show()
            stopSelf()
        }

        contentLayout.addView(cameraButton)
        contentLayout.addView(pill)
        container.addView(contentLayout)
        rootView = container

        // Touch & Drag Handling
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isDragging = false

        cameraButton.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false

                    // Schedule long press to reveal "Gizle" pill
                    longPressRunnable = Runnable {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        pill.visibility = if (pill.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                    }
                    mainHandler.postDelayed(longPressRunnable!!, 550)
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY

                    if (!isDragging && hypot(dx.toDouble(), dy.toDouble()) > touchSlop) {
                        isDragging = true
                        longPressRunnable?.let { mainHandler.removeCallbacks(it) }
                        pill.visibility = View.GONE
                    }

                    if (isDragging) {
                        layoutParams.x = (initialX + dx).toInt()
                        layoutParams.y = (initialY + dy).toInt()
                        windowManager?.updateViewLayout(rootView, layoutParams)
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    longPressRunnable?.let { mainHandler.removeCallbacks(it) }

                    if (!isDragging) {
                        // User performed a single tap!
                        if (pill.visibility == View.VISIBLE) {
                            pill.visibility = View.GONE
                        } else {
                            handleScreenshotTap()
                        }
                    } else {
                        // Save last position
                        prefs.edit()
                            .putInt(PREF_X, layoutParams.x)
                            .putInt(PREF_Y, layoutParams.y)
                            .apply()
                    }
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    longPressRunnable?.let { mainHandler.removeCallbacks(it) }
                    true
                }

                else -> false
            }
        }

        windowManager?.addView(rootView, layoutParams)
    }

    private fun handleScreenshotTap() {
        if (!ScreenshotServiceHolder.isServiceConnected()) {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            return
        }

        // Temporarily hide the floating button so it doesn't appear in the captured screenshot
        rootView?.visibility = View.INVISIBLE

        mainHandler.postDelayed({
            ScreenshotServiceHolder.takeScreenshotDirect()

            // Restore floating button after system capture completes
            mainHandler.postDelayed({
                rootView?.visibility = View.VISIBLE
            }, 500)
        }, 120)
    }

    private fun removeFloatingView() {
        rootView?.let { view ->
            try {
                windowManager?.removeView(view)
            } catch (_: Exception) {
            }
            rootView = null
        }
    }
}
