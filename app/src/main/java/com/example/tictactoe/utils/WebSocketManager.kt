
import android.os.Handler
import android.os.Looper
import okhttp3.*
import okio.ByteString
import org.json.JSONObject

interface WebSocketResponseListener {
    fun onRoomCreated(roomCode: String)
    fun onResponse(message: String)

    fun onOpen(message: Response)
    fun onFailure(error: Throwable)

    fun onclose(message: String)
}

class WebSocketManager(private val token: String, private val listener: WebSocketResponseListener) {
    private val wsUrl = "ws://192.168.237.188:8088?token=$token"
    private lateinit var webSocket: WebSocket
    private var isConnected: Boolean = false

    fun isWebSocketConnected(): Boolean {
        return isConnected
    }
    fun connectWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url(wsUrl).build()
        val webSocketListener = MyWebSocketListener()
        webSocket = client.newWebSocket(request, webSocketListener)
    }

    fun sendMessage(message: String) {
        webSocket.send(message)
    }

    fun closeWebSocket() {
        webSocket.close(1000, "Connection closed")
    }

    private inner class MyWebSocketListener : WebSocketListener() {
        private var reconnectAttempts = 0
        private val maxReconnectAttempts = 6 // You can set a max limit
        private val reconnectInterval = 5000L // Time in milliseconds

        override fun onOpen(webSocket: WebSocket, response: Response) {
            isConnected = true
//            listener.onResponse("{\"message\":\"connected to socket\"}")
        }

        private fun attemptReconnect() {
            if (reconnectAttempts < maxReconnectAttempts) {
                reconnectAttempts++
                // Delay reconnection to give the network time to recover
                Handler(Looper.getMainLooper()).postDelayed({
                    if(!isWebSocketConnected()){
                        connectWebSocket()

                    }
                }, reconnectInterval)
            } else {
                listener.onFailure(Exception("Max reconnection attempts reached"))
            }
        }




        override fun onMessage(webSocket: WebSocket, text: String) {


            if (text.startsWith("{\"createroomCode\":")) {
                // Parsing the JSON formatted message
                val jsonObject = JSONObject(text)
                val roomCode = jsonObject.getString("createroomCode")
                listener.onRoomCreated(roomCode)
            }else {
                listener.onResponse(text)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {

            isConnected = false
            listener.onclose(reason)
//            super.onClosed(webSocket, code, reason)
            attemptReconnect()

        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            isConnected = false

//            super.onClosing(webSocket, code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            listener.onFailure(t)
            attemptReconnect()

        }
    }


}
