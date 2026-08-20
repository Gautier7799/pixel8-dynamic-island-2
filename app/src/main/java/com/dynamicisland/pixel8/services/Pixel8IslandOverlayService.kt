package com.dynamicisland.pixel8.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.dynamicisland.pixel8.R

class Pixel8IslandOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var islandView: View? = null
    private lateinit var params: WindowManager.LayoutParams
    private var isExpanded = false

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val title = intent?.getStringExtra("title") ?: "إشعار جديد"
            val message = intent?.getStringExtra("message") ?: ""
            showNotificationIsland(title, message)
        }
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupPixel8CutoutOverlay()

        val filter = IntentFilter("com.dynamicisland.pixel8.NOTIFICATION_RECEIVED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(notificationReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(notificationReceiver, filter)
        }
    }

    private fun setupPixel8CutoutOverlay() {
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // إحداثيات ومقاسات كاميرا هاتف Google Pixel 8 (Poinçon Centré)
        params = WindowManager.LayoutParams(
            dpToPx(115), // العرض الافتراضي
            dpToPx(34),  // الارتفاع الافتراضي
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = dpToPx(11) // المسافة من أعلى الشاشة
        }

        islandView = LayoutInflater.from(this).inflate(R.layout.view_pixel8_island, null)
        
        // عند الضغط على الجزيرة: التبديل بين التكبير والتصغير
        islandView?.setOnClickListener {
            if (isExpanded) collapseIsland() else expandIsland(280, 80)
        }

        windowManager.addView(islandView, params)
    }

    fun showNotificationIsland(title: String, message: String) {
        expandIsland(320, 90)
        islandView?.findViewById<TextView>(R.id.islandTitle)?.text = title
        islandView?.findViewById<TextView>(R.id.islandSubtitle)?.text = message

        // الإغلاق التلقائي بعد 4 ثوانٍ
        islandView?.postDelayed({
            collapseIsland()
        }, 4000)
    }

    fun expandIsland(widthDp: Int, heightDp: Int) {
        isExpanded = true
        params.width = dpToPx(widthDp)
        params.height = dpToPx(heightDp)
        islandView?.findViewById<View>(R.id.expandedContent)?.visibility = View.VISIBLE
        windowManager.updateViewLayout(islandView, params)
    }

    fun collapseIsland() {
        isExpanded = false
        params.width = dpToPx(115)
        params.height = dpToPx(34)
        islandView?.findViewById<View>(R.id.expandedContent)?.visibility = View.GONE
        windowManager.updateViewLayout(islandView, params)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
        if (islandView != null) {
            windowManager.removeView(islandView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
