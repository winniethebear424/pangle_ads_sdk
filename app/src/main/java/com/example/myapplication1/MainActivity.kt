package com.example.myapplication1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.openadsdk.*
import com.bytedance.sdk.openadsdk.api.init.PAGConfig
import com.bytedance.sdk.openadsdk.api.init.PAGSdk
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdInteractionListener
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdLoadListener
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialRequest

class MainActivity : AppCompatActivity() {
    private val TAG = "PangleAdsDemo"
    private val APP_ID = "8025677"
    private val PLACEMENT_ID = "980088188"
    private var mInterstitialAd: PAGInterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Pangle SDK
        initPangleSdk()

        // Set up load ad button
        val loadAdButton = findViewById<Button>(R.id.load_ad_button)
        loadAdButton.setOnClickListener {
            loadInterstitialAd()
        }
    }

    private fun initPangleSdk() {
        // Create PAG config
        val config = PAGConfig.Builder()
            .appId(APP_ID)
            .debugLog(true) // Enable debug logs for testing
            .build()

        // Initialize SDK
        PAGSdk.init(this, config, object : PAGSdk.PAGInitCallback {
            override fun success() {
                Log.d("pangle", "Pangle SDK initialized successfully")
                Toast.makeText(this@MainActivity, "SDK initialized successfully", Toast.LENGTH_SHORT).show()
            }

            override fun fail(code: Int, message: String) {
                Log.e("pangle", "Pangle SDK initialization failed: $code, $message")
                Toast.makeText(this@MainActivity, "SDK initialization failed: $message", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadInterstitialAd() {
        // Create ad request
        val adRequest = PAGInterstitialRequest()

        // Load interstitial ad
        PAGInterstitialAd.loadAd(
            PLACEMENT_ID,
            adRequest,
            object : PAGInterstitialAdLoadListener {
                override fun onError(code: Int, message: String) {
                    Log.e("pangle", "Failed to load interstitial ad: $code, $message")
                    Toast.makeText(this@MainActivity, "Ad load failed: $message", Toast.LENGTH_SHORT).show()
                }

                override fun onAdLoaded(pagInterstitialAd: PAGInterstitialAd) {
                    Log.d("pangle", "Interstitial ad loaded successfully")
                    Toast.makeText(this@MainActivity, "Ad loaded successfully", Toast.LENGTH_SHORT).show()
                    mInterstitialAd = pagInterstitialAd

                    // Show the ad immediately after it's loaded
                    showInterstitialAd()
                }
            }
        )
    }

    private fun showInterstitialAd() {
        mInterstitialAd?.let { ad ->
            ad.setAdInteractionListener(object : PAGInterstitialAdInteractionListener {
                override fun onAdShowed() {
                    Log.d(TAG, "Interstitial ad showed")
                }

                override fun onAdClicked() {
                    Log.d(TAG, "Interstitial ad clicked")
                }

                override fun onAdDismissed() {
                    Log.d(TAG, "Interstitial ad dismissed")
                    mInterstitialAd = null
                }
            })

            // Show the ad
            ad.show(this)
        } ?: run {
            Log.e(TAG, "Interstitial ad not loaded yet")
            Toast.makeText(this, "Ad not loaded yet", Toast.LENGTH_SHORT).show()
        }
    }
}