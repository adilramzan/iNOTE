package com.example.iNOTE.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.iNOTE.R
import com.example.iNOTE.viewModel.AuthViewModel

class SplashActivity : AppCompatActivity() {

    private var isSplashVisible = true
    private val  authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen: SplashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { isSplashVisible }

        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            isSplashVisible = false
            if (authViewModel.isUserLoggedIn()) {
                val intent = Intent(this, HomeActivity::class.java)
                val options = android.app.ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                startActivity(intent,options.toBundle())
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                val options = android.app.ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                startActivity(intent,options.toBundle())
            }
            finish()
        }, 1500)


    }
}