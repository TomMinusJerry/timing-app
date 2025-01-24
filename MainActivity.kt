import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var webSocket: WebSocket
    private var startTime: Date? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupWebSocket()
    }

    private fun setupWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url("wss://timing-app-375791a90102.herokuapp.com").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                if (text == "start") {
                    startTimer()
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                println("WebSocket closed: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                println("WebSocket error: ${t.message}")
            }
        })
    }

    private fun startTimer() {
        startTime = Date()
        timerRunnable = object : Runnable {
            override fun run() {
                val elapsed = Date().time - startTime!!.time
                println("Elapsed Time: $elapsed ms")
                handler.postDelayed(this, 10) // Update every 10ms
            }
        }
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
        webSocket.send("stop")
    }
}
