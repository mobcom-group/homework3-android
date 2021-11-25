package com.lazudanizaidan.chatapplication

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var listViewType: MutableList<Int>
    private lateinit var listChat: MutableList<Chat>
    private lateinit var adapterChat: AdapterChat

    private val requestCodeGallery = 1
    private val requestCodeCamera = 2
    private val requestCodePermission = 100

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val message = intent?.getStringExtra("message");
        if (message != null) {
            sendMessageFromTopicToChat(message)
        }
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val receivedMessage = intent.getStringExtra("message")
            if (receivedMessage != null) {
                sendMessageFromTopicToChat(receivedMessage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, IntentFilter("message"));
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

        checkRuntimePermissions()
        val editText: EditText = findViewById(R.id.editText)
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendTextMessage()
            }
            true
        }
        val buttonSendMessage: Button = findViewById(R.id.button_send_message_activity_main)
        buttonSendMessage.setOnClickListener {
            sendTextMessage()
        }
        val buttonSendImage: Button = findViewById(R.id.button_send_image_activity_main)
        buttonSendImage.setOnClickListener {
            sendImageMessage()
        }
        setupAdapterRecyclerView()
    }

    private fun checkRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), requestCodePermission)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.forEach {
            if (it == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "This permissions is needed for full functional app", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun sendImageMessage() {
        val radioGroup: RadioGroup = findViewById(R.id.radio_group_activity_main)
        val idTypeChat = radioGroup.checkedRadioButtonId
        val typeChat = if (idTypeChat == R.id.radio_button_my_self_activity_main) {
            AdapterChat.VIEW_TYPE_MY_SELF
        } else {
            AdapterChat.VIEW_TYPE_USER
        }
        val builderAlertDialog = AlertDialog.Builder(this)
            .setTitle("Pilih Aksi")
            .setItems(arrayOf("Gallery", "Kamera")) { dialogInterface, indexItem ->
                dialogInterface.dismiss()
                when (indexItem) {
                    0 -> {
                        val intentImagePicker = Intent(Intent.ACTION_PICK)
                        intentImagePicker.type = "image/*"
                        startActivityForResult(intentImagePicker, requestCodeGallery)
                    }
                    1 -> {
                        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intentCamera, requestCodeCamera)
                    }
                }
            }
        val alertDialog = builderAlertDialog.create()
        alertDialog.show()
    }

    private fun sendTextMessage() {
        val radioGroup: RadioGroup = findViewById(R.id.radio_group_activity_main)
        val idTypeChat = radioGroup.checkedRadioButtonId
        val typeChat = if (idTypeChat == R.id.radio_button_my_self_activity_main) {
            AdapterChat.VIEW_TYPE_MY_SELF
        } else {
            AdapterChat.VIEW_TYPE_USER
        }
        val editText: EditText = findViewById(R.id.editText)
        val message = editText.text.toString().trim()
        if (message.isEmpty()) {
            Toast.makeText(this@MainActivity, "Message is empty", Toast.LENGTH_SHORT)
                .show()
        } else {
            val dateTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US)
                .format(Date())
            val chat = Chat(message = message, dateTime = dateTime, image = "")
            listViewType.add(typeChat)
            listChat.add(chat)
            adapterChat.notifyDataSetChanged()
        }
    }

    private fun setupAdapterRecyclerView() {
        listViewType = mutableListOf()
        listChat = mutableListOf()
        adapterChat = AdapterChat(listViewType = listViewType, listChat = listChat)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_chat_activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterChat
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                requestCodeGallery -> {
                    val fileImage = getBitmapFile(data)
                    if (fileImage != null) {
                        val radioGroup: RadioGroup = findViewById(R.id.radio_group_activity_main)
                        val idTypeChat = radioGroup.checkedRadioButtonId
                        val typeChat = if (idTypeChat == R.id.radio_button_my_self_activity_main) {
                            AdapterChat.VIEW_TYPE_MY_SELF
                        } else {
                            AdapterChat.VIEW_TYPE_USER
                        }
                        val message = ""
                        val dateTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US)
                            .format(Date())
                        val chat = Chat(message = message, dateTime = dateTime, image = fileImage.absolutePath)
                        listViewType.add(typeChat)
                        listChat.add(chat)
                        adapterChat.notifyDataSetChanged()
                    }
                }
                requestCodeCamera -> {
                    val thumbnail = data?.extras!!["data"] as Bitmap
                    val baos = ByteArrayOutputStream()
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, baos)
                    val dir = File("${Environment.getExternalStorageDirectory()}/ChatLayout")
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }

                    try {
                        val fileImage = File(dir, "${Calendar.getInstance().timeInMillis}.jpg")
                        fileImage.createNewFile()
                        val fileOutputStream = FileOutputStream(fileImage)
                        fileOutputStream.write(baos.toByteArray())

                        MediaScannerConnection.scanFile(this, arrayOf(fileImage.path), arrayOf("image/jpeg"), null)
                        fileOutputStream.close()

                        val radioGroup: RadioGroup = findViewById(R.id.radio_group_activity_main)
                        val idTypeChat = radioGroup.checkedRadioButtonId
                        val typeChat = if (idTypeChat == R.id.radio_button_my_self_activity_main) {
                            AdapterChat.VIEW_TYPE_MY_SELF
                        } else {
                            AdapterChat.VIEW_TYPE_USER
                        }
                        val message = ""
                        val dateTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US)
                            .format(Date())
                        val chat = Chat(message = message, dateTime = dateTime, image = fileImage.absolutePath)
                        listViewType.add(typeChat)
                        listChat.add(chat)
                        adapterChat.notifyDataSetChanged()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getBitmapFile(data: Intent?): File? {
        data?.let {
            val selectedImage = it.data
            val cursor = selectedImage?.let { it1 ->
                contentResolver
                    .query(it1, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            }
            cursor?.moveToFirst()

            val index = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val selectedImagePath = index?.let { it1 -> cursor?.getString(it1) }
            cursor?.close()
            return File(selectedImagePath)
        }
        return null
    }

    private fun sendMessageFromTopicToChat(message: String) {
        val typeChat = AdapterChat.VIEW_TYPE_USER
        val dateTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US)
            .format(Date())
        val chat = Chat(message = message, dateTime = dateTime, image = "")
        listViewType.add(typeChat)
        listChat.add(chat)
        adapterChat.notifyDataSetChanged()
    }

}