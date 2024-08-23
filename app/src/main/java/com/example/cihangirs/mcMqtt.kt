package com.example.cihangirs

import android.content.Context
import android.util.Log

//import org.eclipse.paho.android.service.MqttAndroidClient
import info.mqtt.android.service.MqttAndroidClient
import info.mqtt.android.service.QoS
import org.eclipse.paho.client.mqttv3.*
import java.io.Serializable

class mcMqtt(context: Context, serverUri: String) {

    companion object {
        const val TAG = "mcMqtt"
        private var clientId = "Cihangir_" + userName
    }

    private lateinit var mqttClient: MqttAndroidClient
    private lateinit var msgListener: OnMessageArrivedListener

    interface OnMessageArrivedListener {
        fun onMessageArrived(topic: String, message: String)
    }

    init {
        Log.d(TAG, serverUri)
        mqttClient = MqttAndroidClient(context, serverUri, clientId)
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.d(TAG, s)
            }
            override fun connectionLost(throwable: Throwable) {}
            //@Throws(Exception::class)
            override fun messageArrived(topic: String,mqttMessage: MqttMessage) {
                Log.d(TAG, mqttMessage.toString())
                msgListener.onMessageArrived(topic, mqttMessage.toString())
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
        })
        //connect()
    }
    //--------------------------------------------------------------------------------------------//
    fun setMessageListener(messageArrivedListener: OnMessageArrivedListener) {
        msgListener = messageArrivedListener
    }
    //--------------------------------------------------------------------------------------------//
    public fun connect(topic: String) {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.connectionTimeout = 30
        mqttConnectOptions.keepAliveInterval = 60
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false

        mqttClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(TAG, "Connection success")
                val disconnectedBufferOptions = DisconnectedBufferOptions()
                disconnectedBufferOptions.isBufferEnabled = true
                disconnectedBufferOptions.bufferSize = 100
                disconnectedBufferOptions.isPersistBuffer = false
                disconnectedBufferOptions.isDeleteOldestMessages = false
                mqttClient.setBufferOpts(disconnectedBufferOptions)
                subscribe(topic)
            }
            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e(TAG, "Connection failure")
            }
        })
    }
    //--------------------------------------------------------------------------------------------//
    fun disconnect(){
        if(mqttClient.isConnected != false){
            Log.d(TAG, "MQTT Client is already disconnected")
            return
        }
        mqttClient.disconnect(null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(TAG, "Disconnected")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(TAG, "Failed to disconnect")
            }
        })
    }
    //--------------------------------------------------------------------------------------------//
    private fun subscribe(topic: String, qos: Int = 1) {

        if(mqttClient.isConnected != true){
            Log.d(TAG, "MQTT Client is not connected")
            return
        }
        mqttClient.unsubscribe(topic)
        mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(TAG, "Subscribed to $topic")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(TAG, "Failed to subscribe $topic")
            }
        })
    }
    //--------------------------------------------------------------------------------------------//
    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        if(mqttClient.isConnected != true){
            Log.d(TAG, "MQTT Client is not connected")
            return
        }

        val message = MqttMessage()
        message.payload = msg.toByteArray()
        message.qos = qos
        message.isRetained = retained
        mqttClient.publish(topic, message, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(TAG, "$msg published to $topic")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d(TAG, "Failed to publish $msg to $topic")
            }
        })

    }


}