package com.example.myapplication1
//
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import java.net.URL;
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sdk.openadsdk.api.init.PAGConfig
import com.bytedance.sdk.openadsdk.api.init.PAGSdk
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdLoadListener
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialRequest
import java.net.HttpURLConnection
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("Pangle", "working!")

        // Check internet connection
        checkInternet()

        // initialize sdk
        val pAGInitConfig = buildNewConfig()

        PAGSdk.init(applicationContext, pAGInitConfig, object: PAGSdk.PAGInitCallback{
            override fun success() {
                Log.i("Pangle", "SDK initialized successfully!")
            }
            override fun fail(code: Int, msgs: String?){
                Log.i("Pangle", "Failed SDK initialization" + code)
            }
        })

        // set listening on btn
        val btnShowAd = findViewById<Button>(R.id.btn_show_ad)
        btnShowAd.setOnClickListener {
            Log.i("Pangle", "button clicked")
            loadAndShowAd()
        }
    }

    private fun checkInternet(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val address = InetAddress.getByName("google.com")
                Log.d("Network", "Internet is availavle: $address")
                // Switching to main thread to update UI
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Internet is available", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Network", "Error checking internet avalability", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadAndShowAd() {
        val request = PAGInterstitialRequest()
        Log.i("Pangle", "Request created: $request")

        val context = applicationContext

        PAGInterstitialAd.loadAd("980088188", request, object: PAGInterstitialAdLoadListener {
            override fun onError(code: Int, msgs: String?){
                Log.i("Pangle", "Loading error! $code, msgs: ${msgs?: "No message"}")
            }

            override fun onAdLoaded(ad: PAGInterstitialAd?) {
                if (ad != null) {
                    Log.i("Pangle", "Loading successfully")

                    val activity = context as? Activity
                    if ((context as? AppCompatActivity)?.isFinishing == true || (context as? AppCompatActivity)?.isDestroyed == true) {
                        Log.w("Pangle", "Activity is finishing or destroyed, cannot show ad.")
                        return
                    }
                    ad.show(activity)
                } else {
                    Log.i("Pangle", "Ad is null, cannot show.")
                }
            }
        })
    }

    private fun buildNewConfig(): PAGConfig {
        return PAGConfig.Builder()
            .appId("8025677")
            .appIcon(R.mipmap.ic_launcher)
            .debugLog(true)
            .build()
    }


    // Check if the internet is available
    private fun isInternetAvailable(): Boolean {
        return try {
            val url = URL("https://www.google.com")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 1500
            connection.readTimeout = 1500
            connection.connect()
            val responseCode = connection.responseCode
            connection.disconnect()

            // If the response code is HTTP_OK, the internet is available
            responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            Log.e("InternetCheck", "Error checking internet availability", e)
            false
        }
    }
}