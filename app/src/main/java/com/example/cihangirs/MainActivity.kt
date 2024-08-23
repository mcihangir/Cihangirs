package com.example.cihangirs

//import android.R
import android.R.attr.value
import android.app.AlertDialog
import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


var myMqtt: mcMqtt? = null
var userName: String? = null//"Mehmet"

var recMeh: String? = " "
var recYas: String? = " "
var recFer: String? = " "
var recAhm: String? = " "

//var recMesg: String? = null

var userStatus = mutableMapOf("Mehmet" to 0, "Yasemin" to 0, "Ferman" to 0, "Ahmet" to 0)

class MainActivity : AppCompatActivity(), mcMqtt.OnMessageArrivedListener {
    companion object {
        const val TAG = "AndroidMqttClient"
    }
    private var stateAhmet = 0
    private var topic: String? = null


    lateinit var imageMehmet: ImageView
    lateinit var imageYasemin: ImageView
    lateinit var imageFerman: ImageView
    lateinit var imageAhmet: ImageView

    lateinit var sourceMehmet: ImageDecoder.Source
    lateinit var drawableMehmet: Drawable
    lateinit var sourceYasemin: ImageDecoder.Source
    lateinit var drawableYasemin: Drawable
    lateinit var sourceFerman: ImageDecoder.Source
    lateinit var drawableFerman: Drawable
    lateinit var sourceAhmet: ImageDecoder.Source
    lateinit var drawableAhmet: Drawable
    //--------------------------------------------------------------------------------------------//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var helloText : TextView = findViewById(R.id.textViewHello)
        imageMehmet = findViewById(R.id.imageViewMehmet)
        imageYasemin = findViewById(R.id.imageViewYasemin)
        imageFerman = findViewById(R.id.imageViewFerman)
        imageAhmet = findViewById(R.id.imageViewAhmet)

        sourceMehmet = ImageDecoder.createSource(resources, R.drawable.mmod)
        drawableMehmet = ImageDecoder.decodeDrawable(sourceMehmet)
        sourceYasemin = ImageDecoder.createSource(resources, R.drawable.ymod)
        drawableYasemin = ImageDecoder.decodeDrawable(sourceYasemin)
        sourceFerman = ImageDecoder.createSource(resources, R.drawable.fmod)
        drawableFerman = ImageDecoder.decodeDrawable(sourceFerman)
        sourceAhmet = ImageDecoder.createSource(resources, R.drawable.amod)
        drawableAhmet = ImageDecoder.decodeDrawable(sourceAhmet)

        // get data from share SharePreference
        val sp = getSharedPreferences("USER_NAME", MODE_PRIVATE)
        userName = sp.getString("username", null)
        Log.d(TAG, "UserName: $userName")
        helloText.setText("Hello $userName!")
        myMqtt = mcMqtt(this, getString(R.string.mqtt_server))
        myMqtt!!.setMessageListener(this)
        connectToTopic()

        //myMqtt!!.connect("cihangir/mehmet")

        val intent = Intent(this, ChatActivity::class.java )

        imageMehmet.setOnClickListener {
            userStatus["Mehmet"] = 1
            intent.putExtra("name", "Mehmet")
            startActivity(intent)
            imageMehmet.setImageResource(R.drawable.mehmet)
            //myMqtt!!.setMessageListener(this)
            //finishActivity()
        }
        imageYasemin.setOnClickListener {
            userStatus["Yasemin"] = 1
            intent.putExtra("name", "Yasemin")
            //intent.putExtra("activity", this)
            startActivity(intent)
            imageYasemin.setImageResource(R.drawable.yasemin)
            //myMqtt!!.setMessageListener(this)
            //finishActivity(intent)
        }
        imageFerman.setOnClickListener {
            userStatus["Ferman"] = 1
            intent.putExtra("name", "Ferman")
            startActivity(intent)
            imageFerman.setImageResource(R.drawable.ferman)
            //myMqtt!!.setMessageListener(this)
            //finishActivity(intent)
        }
        imageAhmet.setOnClickListener {
            userStatus["Ahmet"] = 1
            intent.putExtra("name", "Ahmet")
            startActivity(intent)
            imageAhmet.setImageResource(R.drawable.ahmet)
            //myMqtt!!.setMessageListener(this)
            //finishActivity(intent)
        }
    }
    //--------------------------------------------------------------------------------------------//
    override fun onDestroy() {
        //Timber.d("onDestroy")
        myMqtt!!.disconnect()
        super.onDestroy()
    }
    //--------------------------------------------------------------------------------------------//
    // Implement the interface method
    override fun onMessageArrived(topic: String, message: String) {
        Log.d(TAG, "[$topic] [$message]")
        // You can also update the UI here based on the message
        val sender = message.subSequence(0, 3)
        Log.d(TAG, "sender: $sender")

        //recMesg += "\n ${message.substring(3)}"
        when(sender){
            "Meh" -> {
                if(userStatus["Mehmet"] == 0) {
                    imageMehmet.post {
                        imageMehmet.setImageDrawable(drawableMehmet)
                        (drawableMehmet as? AnimatedImageDrawable)?.start()
                    }
                }
                //recMeh += "\n $message"
                recMeh += "\n${message.substring(3)}"
            }
            "Yas" -> imageYasemin.post {
                if(userStatus["Yasemin"] == 0) {
                    imageYasemin.setImageDrawable(drawableYasemin)
                    (drawableYasemin as? AnimatedImageDrawable)?.start()
                }
                recYas += "\n${message.substring(3)}"
            }
            "Fer" -> imageFerman.post {
                if(userStatus["Ferman"] == 0) {
                    imageFerman.setImageDrawable(drawableFerman)
                    (drawableFerman as? AnimatedImageDrawable)?.start()
                }
                recFer += "\n${message.substring(3)}"
            }
            "Ahm" -> imageAhmet.post {
                if(userStatus["Ahmet"] == 0) {
                    imageAhmet.setImageDrawable(drawableAhmet)
                    (drawableAhmet as? AnimatedImageDrawable)?.start()
                }
                recAhm += "\n${message.substring(3)}"
            }
        }
    }
    //--------------------------------------------------------------------------------------------//
    private fun connectToTopic(){

        if(userName == null) {
            // initialise the alert dialog builder
            val builder = AlertDialog.Builder(this)

            // set the title for the alert dialog
            builder.setTitle("Kimsiniz?")

            // set the icon for the alert dialog
            //builder.setIcon(R.drawable.image_logo)

            // initialise the list items for the alert dialog
            val listItems = arrayOf("Mehmet", "Yasemin", "Ferman", "Ahmet")
            val checkedItems = BooleanArray(listItems.size)

            // copy the items from the main list to the selected item list for the preview
            // if the item is checked then only the item should be displayed for the user
            val selectedItems = mutableListOf(*listItems)


            // add a radio button list
            builder.setSingleChoiceItems(listItems, 0) { dialog, which ->
                // user checked an item
                for (i in checkedItems.indices) {
                    checkedItems[i] = false
                }
                checkedItems[which] = true
            }

            // alert dialog shouldn't be cancellable
            builder.setCancelable(false)

            // handle the positive button of the dialog
            builder.setPositiveButton("Done") { dialog, which ->
                for (i in checkedItems.indices) {
                    if (checkedItems[i]) {
                        userName = selectedItems[i]
                        Log.d(TAG, "User: $userName")
                        topic = "cihangir/" + userName
                        myMqtt!!.connect(topic!!)
                        Log.d(TAG, "Topic: $topic")

                        // save data into share SharePreference
                        val sp = getSharedPreferences("USER_NAME", MODE_PRIVATE)
                        val edit = sp.edit()
                        edit.putString("username", userName)
                        edit.apply()
                    }
                }
            }

            // create the builder
            builder.create()
            builder.show()
        } else {
            topic = "cihangir/" + userName
            myMqtt!!.connect(topic!!)
            Log.d(TAG, "Topic: $topic")
        }

    }
    //--------------------------------------------------------------------------------------------//

}