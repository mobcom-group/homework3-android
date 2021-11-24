package com.lazudanizaidan.chatapplication

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
        setContentView(R.layout.activity_main)

        var editText = R.id.editText
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val idTypeChat = radio_group_activity_main.checkedRadioButtonId
                val typeChat = if (idTypeChat == R.id.radio_button_my_self_activity_main) {
                    AdapterChat.VIEW_TYPE_MY_SELF
                } else {
                    AdapterChat.VIEW_TYPE_USER
                }
                val message = editText.toString().trim()
                if (message.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Message is empty", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val dateTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US)
                        .format(Date())
                    val chat = Chat(message = message, dateTime = dateTime)
                    val listViewType = mutableListOf()
                    val listChat = mutableListOf()
                    val adapterChat = AdapterChat(listViewType = listViewType, listChat = listChat)
                    listViewType.add(typeChat)
                    listChat.add(chat)
                    adapterChat.notifyDataSetChanged()
                }
            }
            true
        }
        setupAdapterRecyclerView()
    }

    private fun setupAdapterRecyclerView() {
        val listViewType = mutableListOf()
        val listChat = mutableListOf()
        val adapterChat = AdapterChat(listViewType = listViewType, listChat = listChat)
        var recyclerView : RecyclerView
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view_chat_activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterChat
    }
}