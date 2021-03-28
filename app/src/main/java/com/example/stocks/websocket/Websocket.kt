package com.example.stocks.websocket

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.net.ssl.SSLSocketFactory

class Websocket {
    @JsonClass(generateAdapter = true)
    data class Result(val data:List<Data>)
    @JsonClass(generateAdapter = true)
    data class Data(val p:Double, val s: String, val t: Long)
    private lateinit var webSocketClient: WebSocketClient

    var answer = MutableLiveData<List<Data>>()
    val stocks = mutableListOf<String>()
     fun initWebSocket() {
        val coinbaseUri = URI(WEB_SOCKET_URL)

        createWebSocketClient(coinbaseUri)
        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        webSocketClient.setSocketFactory(socketFactory)
        webSocketClient.connect()
    }

    private fun createWebSocketClient(coinbaseUri: URI?) {
        webSocketClient = object : WebSocketClient(coinbaseUri) {

            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen")
                subscribe()
            }

            override fun onMessage(message: String?) {
                val moshi = Moshi.Builder().build()
                val adapter: JsonAdapter<Result> = moshi.adapter(Result::class.java)
                val bitcoin = adapter.fromJson(message)
                answer.postValue(bitcoin?.data)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
                unsubscribe()
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "onError: ${ex?.message}")
            }

        }
    }

     fun subscribe() {
        stocks.forEach { webSocketClient.send("{\"type\":\"subscribe\",\"symbol\":\"$it\"}") }
    }

    fun unsubscribe() {
        webSocketClient.send(
            "{\n" +
                    "    \"type\": \"unsubscribe\",\n" +
                    "    \"channels\": [\"ticker\"]\n" +
                    "}"
        )
    }

    companion object {
        const val WEB_SOCKET_URL = "wss://ws.finnhub.io?token=c0mmsm748v6tkq136co0"
        const val TAG = "NICE"
    }
}