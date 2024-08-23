package com.example.cihangirs

import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextPaint
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cihangirs.MainActivity.Companion.TAG
import java.util.Timer

class ChatActivity : AppCompatActivity(), mcMqtt.OnMessageArrivedListener {
    /*companion object {
        const val topic = "topic/test/1"
    }
    */
    var topicTo = "topic/test/1"
    lateinit var imageViewChat: ImageView
    lateinit var textMessage: EditText
    lateinit var recMessage: TextView
    lateinit var buttonSend: Button
    lateinit var buttonExit: Button
    var remoteSide: String = "Mehmet"
    private lateinit var cTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.chat_layout)
        imageViewChat = findViewById(R.id.imageViewChat)
        textMessage = findViewById(R.id.editTextText)
        recMessage = findViewById(R.id.textViewChatBox)
        buttonSend = findViewById(R.id.buttonSend)
        buttonExit = findViewById(R.id.buttonExit)

        //myMqtt!!.setMessageListener(this)

        remoteSide = intent.getSerializableExtra("name") as String
        when(remoteSide){
            "Mehmet" -> {
                imageViewChat.setImageResource(R.drawable.mehmet)
                if(recMeh != null) {
                    recMessage.setText(recMeh)
                }
                topicTo = "cihangir/Mehmet"
            }
            "Yasemin" -> {
                imageViewChat.setImageResource(R.drawable.yasemin)
                if(recYas != null) {
                    recMessage.setText(recYas)
                }
                topicTo = "cihangir/Yasemin"
            }
            "Ferman" -> {
                imageViewChat.setImageResource(R.drawable.ferman)
                if(recFer != null) {
                    recMessage.setText(recFer)
                }
                topicTo = "cihangir/Ferman"
            }
            "Ahmet" -> {
                imageViewChat.setImageResource(R.drawable.ahmet)
                if(recAhm != null) {
                    recMessage.setText(recAhm)
                }
                topicTo = "cihangir/Ahmet"
            }
        }
        buttonSend.setOnClickListener {
            if(myMqtt != null) {
                myMqtt!!.publish (topicTo, "${userName!!.substring(0,3)} ${textMessage.text}")
                textMessage.setText("")
            }else{
                Log.d(mcMqtt.TAG, "There is no MQTT Client")
            }
        }
        buttonExit.setOnClickListener {
            //userStatus.forEach{entry -> entry.value = 0 }
            for (key in userStatus.keys) {
                userStatus[key] = 0
            }
            cTimer.cancel()
            this.finish()
        }

        // Initialize a new CountDownTimer instance
        cTimer =  object : CountDownTimer(16069000, 1000) {
            // Callback fired on regular interval.
            override fun onTick(millisUntilFinished: Long) {
                //val secondsRemaining = millisUntilFinished / 1000
                //Log.d(TAG, "$secondsRemaining second(s) remaining")
                refreshRecTextBox()
            }

            // Callback fired when the time is up.
            override fun onFinish() {
                Log.d(TAG, "Done!!!")
            }
        }
        cTimer.start() // Start the countdown timer

    }
    //--------------------------------------------------------------------------------------------//
    //--------------------------------------------------------------------------------------------//
    // Implement the interface method
    override fun onMessageArrived(topic: String, message: String) {
        Log.d(TAG, "ChatActivity: [$topic] [$message]")
        // You can also update the UI here based on the message
        val sender = message.subSequence(0, 3)
        Log.d(TAG, "sender: $sender")
        //var msg = message.subSequence(3, message.length - 1)
        //recMessage.setText(msg)
        when(message.subSequence(0, 3)){
            "Meh" -> {
                recMeh += "\n${message.substring(3)} )"
                recMessage.setText(recMeh)
            }
            "Yas" -> {
                recYas += "\n${message.substring(3)}"
                recMessage.setText(recYas)
            }
            "Fer" -> {
                recFer += "\n${message.substring(3)}"
                recMessage.setText(recFer)
            }
            "Ahm" -> {
                recAhm += "\n${message.substring(3)}"
                recMessage.setText(recAhm)
            }
        }
    }
    //--------------------------------------------------------------------------------------------//
    private fun refreshRecTextBox(){
        when(remoteSide){
            "Mehmet" -> {
                recMessage.setText(recMeh)
            }
            "Yasemin" -> {
                recMessage.setText(recYas)
            }
            "Ferman" -> {
                recMessage.setText(recFer)
            }
            "Ahmet" -> {
                recMessage.setText(recAhm)
            }
        }
    }
}