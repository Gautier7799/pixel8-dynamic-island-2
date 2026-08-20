package com.dynamicisland.pixel8

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dynamicisland.pixel8.services.Pixel8IslandOverlayService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.btnStartService)
        val btnPermission = findViewById<Button>(R.id.btnPermission)

        btnPermission.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                Toast.makeText(this, "تم منح الصلاحية بنجاح!", Toast.LENGTH_SHORT).show()
            }
        }

        btnStart.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                val serviceIntent = Intent(this, Pixel8IslandOverlayService::class.java)
                startService(serviceIntent)
                Toast.makeText(this, "تم تفعيل Dynamic Island على Pixel 8", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "يرجى تفعيل إذن الظهور فوق التطبيقات أولاً", Toast.LENGTH_LONG).show()
            }
        }
    }
}
