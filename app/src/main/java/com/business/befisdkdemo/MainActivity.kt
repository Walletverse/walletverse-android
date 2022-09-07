package com.business.befisdkdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.walletverse.ui.ui.activity.EnterActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        startActivity(Intent(this, HomeActivity::class.java))
//        startActivity(Intent(this, BeFiWeb2LoginActivity::class.java))
        startActivity(Intent(this, EnterActivity::class.java))
        finish()
    }
}