package space.neuralworld.translator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var webView: WebView
    private lateinit var textToSpeech: TextToSpeech

    private var pendingPermissionRequest: PermissionRequest? = null

    private val microphonePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            val request = pendingPermissionRequest
            pendingPermissionRequest = null

            if (granted) {
                request?.grant(
                    arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                )
            } else {
                request?.deny()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Android Text-to-Speech
        textToSpeech = TextToSpeech(this, this)

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {

            override fun onPermissionRequest(request: PermissionRequest) {

                runOnUiThread {

                    if (request.origin.host != "neuralworld.space") {
                        request.deny()
                        return@runOnUiThread
                    }

                    if (
                        ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        request.grant(
                            arrayOf(
                                PermissionRequest.RESOURCE_AUDIO_CAPTURE
                            )
                        )
                    } else {
                        pendingPermissionRequest = request

                        microphonePermissionLauncher.launch(
                            Manifest.permission.RECORD_AUDIO
                        )
                    }
                }
            }
        }

        // JavaScript → Android bridge
        webView.addJavascriptInterface(
            AndroidTTSBridge(),
            "AndroidTTS"
        )

        webView.loadUrl("https://neuralworld.space/")
    }

    // ---------------------------------------------------------
    // Android Text-to-Speech initialization
    // ---------------------------------------------------------

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            textToSpeech.language = Locale.US
            textToSpeech.setSpeechRate(1.0f)

            textToSpeech.setOnUtteranceProgressListener(
                object : UtteranceProgressListener() {

                    override fun onStart(utteranceId: String?) {
                    }

                    override fun onDone(utteranceId: String?) {
                        stopRobotAnimation()
                    }

                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        stopRobotAnimation()
                    }

                    override fun onError(utteranceId: String?) {
                        stopRobotAnimation()
                    }
                }
            )
        }
    }
    private fun stopRobotAnimation() {
        runOnUiThread {
            webView.evaluateJavascript(
                """
            (function() {
                const robot = document.getElementById('aiRobot');
                if (robot) {
                    robot.classList.remove('speaking');
                }
            })();
            """.trimIndent(),
                null
            )
        }
    }

    // ---------------------------------------------------------
    // JavaScript bridge
    // ---------------------------------------------------------

    inner class AndroidTTSBridge {

        @android.webkit.JavascriptInterface
        fun speak(text: String, language: String) {

            runOnUiThread {

                val locale = when (language) {
                    "es" -> Locale("es", "ES")
                    "en" -> Locale.US
                    "hi" -> Locale("hi", "IN")
                    "ja" -> Locale.JAPAN
                    "zh" -> Locale.SIMPLIFIED_CHINESE
                    else -> Locale.US
                }

                textToSpeech.language = locale

                textToSpeech.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "NeuralWorldTTS"
                )
            }
        }
    }

    override fun onDestroy() {

        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }

        webView.destroy()

        super.onDestroy()
    }
}