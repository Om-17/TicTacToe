//import okhttp3.*
//import okio.ByteString
//
//class WebSocketManager(private val token: String) {
//
//    private val wsUrl = "ws://192.168.69.159:8088?token=$token"
//    private lateinit var webSocket: WebSocket
//
//    fun connectWebSocket() {
//        val client = OkHttpClient()
//
//        val request = Request.Builder().url(wsUrl).build()
//        val webSocketListener = MyWebSocketListener() // Your WebSocket listener implementation
//
//        webSocket = client.newWebSocket(request, webSocketListener)
//
//    }
//
//    fun sendMessage(message: String) {
//        webSocket.send(message)
//    }
//
//    fun closeWebSocket() {
//        webSocket.close(1000, "Connection closed")
//    }
//
//    private inner class MyWebSocketListener : WebSocketListener() {
//        override fun onOpen(webSocket: WebSocket, response: Response) {
//
//
//
//
//            println("WebSocket connection successfully opened")
//        }
//
//        override fun onMessage(webSocket: WebSocket, text: String) {
//            // Handle incoming text message
//            println("Message from server: $text")
//        }
//
//        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//            // Handle incoming binary message
//        }
//
//        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//            // WebSocket connection closed
//        }
//
//        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//            // Handle failure
//        }
//    }
//}
import okhttp3.*
import okio.ByteString
import org.json.JSONObject

interface WebSocketResponseListener {
    fun onRoomCreated(roomCode: String)
    fun onResponse(message: String)
    fun onFailure(error: Throwable)
}

class WebSocketManager(private val token: String, private val listener: WebSocketResponseListener) {
    private val wsUrl = "ws://192.168.204.50:8088?token=$token"
    private lateinit var webSocket: WebSocket

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
        override fun onOpen(webSocket: WebSocket, response: Response) {
            listener.onResponse("WebSocket connection successfully opened")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {

            println(text.startsWith("{\"room_code\":"))
            if (text.startsWith("{\"room_code\":")) {
                // Parsing the JSON formatted message
                val jsonObject = JSONObject(text)
                val roomCode = jsonObject.getString("room_code")
                listener.onRoomCreated(roomCode)
            }else {
                listener.onResponse(text)
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            listener.onFailure(t)
        }
    }
}
