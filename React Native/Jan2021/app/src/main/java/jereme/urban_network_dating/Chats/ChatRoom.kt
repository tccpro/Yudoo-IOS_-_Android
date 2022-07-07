package jereme.urban_network_dating.Chats;

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.devlomi.record_view.*
import com.example.messengerapp.RetrofitInstance
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions.KeyboardListener
import jereme.urban_network_dating.API.HttpHandler
import jereme.urban_network_dating.Adapters.ChatRoomAdapter
import jereme.urban_network_dating.List.MessageModel
import jereme.urban_network_dating.LoginActivity
import jereme.urban_network_dating.LoginActivity.base_url
import jereme.urban_network_dating.NewHomeActivity.profile_id
import jereme.urban_network_dating.R
import jereme.urban_network_dating.Utils.WeiboDialogUtils
import jereme.urban_network_dating.fragments.CameraPostBottomsheetFragment
import kotlinx.android.synthetic.main.activity_chat_room.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ChatRoom : AppCompatActivity() {
    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_NAME = "name"
        const val EXTRA_COUNT = "numb"
    }
    var selectedUserID: String? = null
    var selectedMessageName: String? = null
    var selectedMessageDescription: String? = null
    var addMessageResoponse: String? = null

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var contactName: String
    private lateinit var contactId: String
    private var upLoadServerUri: String? = null
    private var contactNumb: Int = -1
    lateinit var nameOfChannel: String
    val mAdapter = ChatRoomAdapter(ArrayList())
    private var imagepath = ""
    var loadingDialog: Dialog? = null
    private var serverResponseCode = 0
    var uploadPhotoName = ""
    var uploadPhotoNamewithoutExtention = ""
    var rootView: View? = null
    var type: String? = null
//    var progress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        rootView = findViewById(R.id.root_view);
//        val recordView: RecordView = findViewById<View>(R.id.record_view) as RecordView
//        val recordButton: RecordButton = findViewById<View>(R.id.record_button) as RecordButton
//        recordButton.setRecordView(recordView);

//        recordButton.setOnRecordClickListener(object : OnRecordClickListener {
//            override fun onClick(v: View?) {
//                Toast.makeText(this@ChatRoom, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show()
//                //  Log.d("RecordButton", "RECORD BUTTON CLICKED")
//            }
//        })
//
//        recordView.cancelBounds = 8f
//        recordView.setSmallMicColor(Color.parseColor("#c2185b"))

//        //prevent recording under one Second
//        recordView.setLessThanSecondAllowed(false)
//        recordView.setSlideToCancelText("Slide To Cancel")
//        recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0)
//
//        recordView.setOnRecordListener(object : OnRecordListener {
//            override fun onStart() {
//                Log.d("RecordView", "onStart")
//                Toast.makeText(this@ChatRoom, "OnStartRecord", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onCancel() {
//                recordView.setCancelBounds(8F);
//                Toast.makeText(this@ChatRoom, "onCancel", Toast.LENGTH_SHORT).show()
//                Log.d("RecordView", "onCancel")
//            }
//
//            override fun onFinish(recordTime: Long) {
//                val time: String = getHumanTimeText(recordTime)
//                Toast.makeText(this@ChatRoom, "onFinishRecord - Recorded Time is: $time", Toast.LENGTH_SHORT).show()
//                Log.d("RecordView", "onFinish")
//                Log.d("RecordTime", time)
//            }
//
//            override fun onLessThanSecond() {
//                Toast.makeText(this@ChatRoom, "OnLessThanSecond", Toast.LENGTH_SHORT).show()
//                Log.d("RecordView", "onLessThanSecond")
//            }
//        })
//
//        recordView.setOnBasketAnimationEndListener(object : OnBasketAnimationEnd {
//            override fun onAnimationEnd() {
//                Log.d("RecordView", "Basket Animation Finished")
//            }
//        })

        val emojIcon = EmojIconActions(this, rootView, editText, emoji_btn)

        emojIcon.ShowEmojIcon()
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley)
        emojIcon.setKeyboardListener(object : KeyboardListener {
            override fun onKeyboardOpen() {
                //  emojIcon.setUseSystemEmoji(true);
                Log.e("FragmentActivity.TAG", "Keyboard opened!")
            }

            override fun onKeyboardClose() {
                Log.e("FragmentActivity.TAG", "Keyboard closed")
            }
        })

        fetchExtras()
        setupRecyclerView()
        subscribeToChannel()
        setupClickListener()
        back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getHumanTimeText(milliseconds: Long): String {
        return java.lang.String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)))
    }
    private fun fetchExtras() {
        contactName = intent.extras!!.getString(EXTRA_NAME)!!
        contactId = intent.extras!!.getString(EXTRA_ID)!!
        contactNumb = intent.extras!!.getInt(EXTRA_COUNT)!!
        //  contactNumb = intent.extras!!.getInt(EXTRA_COUNT)
        username.setText(contactName)
    }

    private fun setupRecyclerView() {
        with(recyclerViewChat) {
            linearLayoutManager = LinearLayoutManager(this@ChatRoom);
            //  linearLayoutManager.reverseLayout=true;
            //  linearLayoutManager.stackFromEnd=true;
            //  setHasFixedSize(true)
            layoutManager = linearLayoutManager
            //  scrollToPosition(0)
            //  (layoutManager as LinearLayoutManager).setReverseLayout(true);
            adapter = mAdapter
        }
    }

    private fun subscribeToChannel() {
//        val authorizer = HttpAuthorizer("http://192.168.0.105:3000/pusher/auth/private")
//        val authorizer = HttpAuthorizer(nodebase_url + "pusher/auth/private")
//        //  val authorizer = HttpAuthorizer(base_url +"chat/authchat.php")
//        val options = PusherOptions().setAuthorizer(authorizer)
//        options.setCluster("mt1")
//
//        val pusher = Pusher("4623ab31f34d74c8dc88", options)
//        pusher.connect()

//        nameOfChannel = if (Singleton.getInstance().currentUser.count > contactNumb) {
//            "private-" + Singleton.getInstance().currentUser.id + "-" + contactId
//        } else {
//            "private-" + contactId + "-" + Singleton.getInstance().currentUser.id
//        }

        nameOfChannel = if (Integer.parseInt(profile_id) > contactNumb) {
            "private-" + profile_id + "-" + contactId
        } else {
            "private-" + contactId + "-" + profile_id
        }

        selectedUserID=  contactId
//        progress = ProgressDialog(this@ChatRoom)
//        progress?.setTitle(resources.getString(R.string.loading))
//        progress?.setMessage(resources.getString(R.string.messages)+"...")
//        progress?.setCancelable(false) // disable dismiss by tapping outside of the dialog
//        progress?.show()
        progresslayout.visibility = View.VISIBLE
        var task = GetMessage()
        task.execute()
        //  Log.i("ChatRoom", nameOfChannel)

//        pusher.subscribePrivate(nameOfChannel, object : PrivateChannelEventListener {
//            //  override fun onEvent(channelName: String?, eventName: String?, data: String?) {
//            override fun onEvent(event: PusherEvent?) {
//                val jsonObject = JSONObject(event?.data)

//                val messageModel = MessageModel(
//                        jsonObject.getString("message"),
//                        jsonObject.getString("sender_id"),
//                        jsonObject.getString("type"),
//                        jsonObject.getString("photo")
//                        )
//
//                runOnUiThread {
//                    mAdapter.add(messageModel)
//                }
//            }
//
//            override fun onAuthenticationFailure(p0: String?, p1: Exception?) {
//                Log.e("ChatRoom", p1!!.localizedMessage)
//            }
//
//            override fun onSubscriptionSucceeded(p0: String?) {
//                Log.i("ChatRoom", "Successful subscription")
//            }
//
//        }, "new-message")

    }

    private fun setupClickListener() {
        //  val searchTo : EditText = findViewById(R.id.searchTo)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // you can call or do what you want with your EditText here
                // yourEditText...
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (editText.text.toString().equals("")) {
                    sendButton.visibility = View.GONE
                    takecamera.visibility = View.VISIBLE
                } else {
                    sendButton.visibility = View.VISIBLE
                    takecamera.visibility = View.GONE
                }
                // Toast.makeText(applicationContext,editText.text.toString(),Toast.LENGTH_LONG).show()
            }
        })

        takecamera.setOnClickListener {
            //  takePhoto();
            val bottomSheet = CameraPostBottomsheetFragment()
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet")
//            val bottomSheet = BottomsheetFragment()
//            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet")
        }

        sendButton.setOnClickListener {
            if (editText.text.isNotEmpty()) {
//                val jsonObject = JSONObject()
//                jsonObject.put("message", editText.text.toString())
//                jsonObject.put("channel_name", nameOfChannel)
//                jsonObject.put("sender_id", profile_id)
//                jsonObject.put("type", "text")
//                val jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
//                      jsonObject.toString())
//
//                RetrofitInstance.retrofit.sendMessage(jsonBody).enqueue(object : Callback<String> {
//                      override fun onFailure(call: Call<String>?, t: Throwable?) {
//                            Log.e("ChatRoom", t!!.localizedMessage)
//                      }
//
//                      override fun onResponse(call: Call<String>?, response: Response<String>?) {
//                            Log.e("ChatRoom", response!!.body() + "")
//                      }
//                })

                val task = SendMessage()
                task.execute()

                selectedUserID = contactId
                selectedMessageName = "text"
                selectedMessageDescription = editText.text.toString()

                val messageModel = MessageModel(
                    editText.text.toString(),
                    profile_id,
                    "text",
                    ""
                )

                runOnUiThread {
                    mAdapter.add(messageModel)
                }
                editText.text.clear()
                hideKeyBoard()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        upLoadServerUri = "https://urban.network/Apinew/upload/upload.php"
        when (requestCode) {
            101 -> {
                Log.v("ProjectDetails", "REQUEST_PICK_IMAGE called")
                if (resultCode == Activity.RESULT_OK) {
                    type = "image"
                    if (data != null) {
                        val photo = data.extras!!["data"] as Bitmap?
                        //imgUser.setImageBitmap(decodeFile(selectedImagePath));
                        try {
                            val file = createImageFile()
                            val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
                            photo?.compress(Bitmap.CompressFormat.JPEG, 100, os)
                            os.close()
//                            loadingDialog = WeiboDialogUtils.createLoadingDialog(this@ChatRoom, "Searching Groups")
//                            loadingDialog?.show()
                            val task = GetLatestPhotoID()
                            task.execute()
                            //  Thread {
                            //      GetLatestPhotoID().execute()
                            //  }.start()
                        } catch (e: java.lang.Exception) {
                            Log.e("error", "Failed to convert images")
                        }
                        } else {
                        Log.v("ProjectDetails", "data is null")
                    }
                }
            }
            102 -> {
                type = "video"
                val videoUri = data!!.data
                imagepath = getRealPathFromURI(videoUri)
                val task = GetLatestPhotoID()
                task.execute()
            }
            2 -> {
                try {
                    val uri = data!!.data
                    val cr: ContentResolver = getContentResolver()
                    val mime = cr.getType(uri!!)
                    if (mime?.split("/".toRegex())?.toTypedArray()?.get(0) == "image") {
                        type = "image"
                        imagepath = getPath(uri)!!
                        val imageStream = contentResolver.openInputStream(uri!!)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        val task = GetLatestPhotoID()
                        task.execute()
                    } else {
                        type = "video"
                        // OI FILE Manager
                        val filemanagerstring = uri.path.toString()
                        imagepath = filemanagerstring

                        // MEDIA GALLERY
                        val selectedVideoPath: String = getVideoPath(uri)
                        if (selectedVideoPath != null) {
                            imagepath = selectedVideoPath
                        }

                        val task = GetLatestPhotoID()
                        task.execute()
                        //  imagepath = getVideoPath(uri);
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getVideoPath(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = uri?.let { getContentResolver().query(it, projection, null, null, null) }
        return if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } else ""
    }

    fun getRealPathFromURI(contentUri: Uri?): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir     /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        imagepath = image.absolutePath
        return image
    }

    fun getPath(uri: Uri?): String? {
        var cursor: Cursor? = uri?.let { getContentResolver().query(it, null, null, null, null) }
        if (cursor != null) {
            cursor.moveToFirst()
        }

        var document_id = cursor?.getString(0)

        if (document_id != null) {
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        }

        if (cursor != null) {
            cursor.close()
        }

        var path: String? = ""

        cursor = getContentResolver().query(
                 MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media._ID + " = ? ",
                 arrayOf(document_id),
                null
        )!!

        cursor.moveToFirst()
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor.close()
        return path
    }

    inner class SendMessage : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            super.onPreExecute()

        }

        protected override fun doInBackground(vararg p0: Void?): Void? {
            val sh = HttpHandler()
            val sub_url = "users/messageadd.php?"
            val parameters = "user=" + profile_id +
                    "&user4=" + selectedUserID +
                    "&name=" + selectedMessageName +
                    "&message=" + selectedMessageDescription +
                    "&type=message"

            val url = base_url + sub_url + parameters
            val jsonStr = sh.makeServiceCall(url)
            if (jsonStr != null) {
                try {
                    val jsonObj = JSONObject(jsonStr)
                    val message_response = jsonObj.getString("m")
                      addMessageResoponse = message_response
                } catch (e: JSONException) {
//                  runOnUiThread(Runnable {
//                        Toast.makeText(getActivity(), "Json parsing error 222: " + e.message,
//                                Toast.LENGTH_LONG).show()
//                    })
                }
            } else {
                //WeiboDialogUtils.closeDialog(loadingDialog)
                runOnUiThread(
                    object : Runnable {
                        override fun run() {
                            Toast.makeText(this@ChatRoom,
                                    "Couldn't get json from server while sending message",
                                    Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            //  WeiboDialogUtils.closeDialog(loadingDialog)
            if (addMessageResoponse == "success") {
                Toast.makeText(applicationContext,resources.getString(R.string.message_send_success),Toast.LENGTH_LONG).show()
//                etMessageDescription.setText("")
////                etMessageTitle.setText("")
//                AlertDialog.Builder(this@ChatRoom)
//                        .setTitle("Send Message")
//                        .setMessage("Sent new message successfully")
//                        .setPositiveButton(android.R.string.yes) { dialog, which -> dialog.dismiss() }
//                        .show()
            } else {
//                AlertDialog.Builder(this@ChatRoom)
//                        .setTitle("Send Message")
//                        .setMessage("can't send message")
//                        .setPositiveButton(android.R.string.yes) { dialog, which -> dialog.dismiss() }
//                        .setNegativeButton(android.R.string.no, null)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show()
            }
        }
    }

    inner class GetMessage : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        protected override fun doInBackground(vararg p0: Void?): Void? {
            val sh = HttpHandler()
            val sub_url = "users/message.php?"
            val parameters = "user=" + profile_id +
                    "&lang=" + LoginActivity.locale_name +
                    "&page=" + 0 +
                    "&flag=" + 0
            val url = base_url + sub_url + parameters
            val jsonStr = sh.makeServiceCall(url)

            if (jsonStr != null) {
                try {
                    val contacts = JSONArray(jsonStr)
                    //  page_count = contacts.length()
                    for (i in 0 until contacts.length()) {
                        val c = contacts.getJSONObject(i)
                        val name = c.getString("name")
                        val message = c.getString("message")
                        val photo = c.getString("photo")
                        val messagedirection = c.getString("messagedirection")
                        val user = c.getString("user")
                        val user4 = c.getString("user4")
                        val partner = c.getString("partner")
                        val date = c.getString("date")
                        val page1 = c.getInt("id")

                        if (user4.equals(selectedUserID) || user.equals(selectedUserID)) {
                            var messageModel: MessageModel;

                            if (messagedirection.equals("income")) {
                                messageModel = MessageModel(message, selectedUserID + "", "text", photo)
                            } else {
                                messageModel = MessageModel(message, profile_id + "", "text", photo)
                            }

                            runOnUiThread {
                                messageModel?.let { mAdapter.add(it) }
                            }
                        }
//                        page = page1
//                        messages.add(MessageFriendList(name, message, photo, messagedirection, user, user4, partner, date))
                    }
                } catch (e: JSONException) {
                    runOnUiThread(Runnable {
                        //  progress?.dismiss()
                        progresslayout.visibility = View.GONE
                        System.out.println("chatroom.kt")
                        Toast.makeText(this@ChatRoom,
                                "You have no Inbox messages  ",
                                Toast.LENGTH_LONG).show()
                    })
                }
            } else {
                //  WeiboDialogUtils.closeDialog(loadingDialog);
                runOnUiThread(Runnable {
                    // progress?.dismiss()
                    progresslayout.visibility = View.GONE
                    Toast.makeText(this@ChatRoom,
                            "Couldn't get json from server while getting messages",
                            Toast.LENGTH_LONG).show()
                })
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
//            progress?.dismiss()
            progresslayout.visibility = View.GONE
//            //   WeiboDialogUtils.closeDialog(loadingDialog);
//            if (messages.size > 0) {
              //  messageFriendListAdapter = MessageFriendListAdapter(getActivity(), messages)
//                messageList.setAdapter(messageFriendListAdapter)
//                //                messageList.setSelection(messages.size() - page_count - 1);
//            }
        }
    }

    var dialog: ProgressDialog? = null
    inner class GetLatestPhotoID() : AsyncTask<Void?, Int?, Void?>() {
        override fun onPreExecute() {
            super.onPreExecute()

            runOnUiThread(Runnable {
                dialog = ProgressDialog(this@ChatRoom)
                dialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                dialog?.setMax(100)
                dialog?.show()
            })
        }

        protected override fun doInBackground(vararg p0: Void?): Void? {
            val sh = HttpHandler()
            val sub_url = "photos/profilePicture.php"
            val parameters = ""
            val url = base_url + sub_url + parameters
            val jsonStr = sh.makeServiceCall(url)

            if (jsonStr != null) {
                try {
                    val jsonObj = JSONObject(jsonStr)
                    val id = jsonObj.getString("id")
                    var a = id.toInt()
                    a = a + 1
                    uploadPhotoName = a.toString()
                    uploadPhotoNamewithoutExtention = uploadPhotoName
                } catch (e: JSONException) {
                     runOnUiThread(Runnable {
                         Toast.makeText(applicationContext, "Json parsing error photoID: " + e.message,
                                 Toast.LENGTH_LONG).show()
                     })
                }
            } else {
                runOnUiThread(Runnable {
                    Toast.makeText(applicationContext, "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG).show()
                })
            }

            return null
        }

        protected override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            values[0]?.let { dialog!!.incrementProgressBy(it) }
        }

        @SuppressLint("WrongThread")
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            publishProgress(50)
            if (imagepath.length > 0) {
                Thread {
//                    loadingDialog = WeiboDialogUtils.createLoadingDialog(this@ChatRoom, "Searching Groups")
//                    loadingDialog?.show()
                    uploadFile(imagepath)
                    dialog?.dismiss()
                }.start()
            } else {
                Toast.makeText(this@ChatRoom, "Select Photo", Toast.LENGTH_SHORT).show()
                WeiboDialogUtils.closeDialog(loadingDialog)
            }
        }
    }

    fun sendMedia(path:String,type: String) {
        val jsonObject = JSONObject()
        jsonObject.put("message", path)
        jsonObject.put("channel_name", nameOfChannel)
        jsonObject.put("sender_id", profile_id)
        jsonObject.put("type", type)
        //val jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString())

        val jsonBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull());

        RetrofitInstance.retrofit.sendMessage(jsonBody).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                Log.e("ChatRoom", t!!.localizedMessage)
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                Log.e("ChatRoom", response!!.body() + "")
            }
        })

        hideKeyBoard()
    }

    fun uploadFile(sourceFileUri: String): Int {
        var fileName = sourceFileUri
        val extention = fileName.substring(fileName.length - 5, fileName.length)
        val separated = extention.split(".")
        fileName = uploadPhotoName + "." + separated[1]
        uploadPhotoName = fileName
        var conn: HttpURLConnection? = null
        var dos: DataOutputStream? = null
        val lineEnd = "\r\n"
        val twoHyphens = "--"
        val boundary = "*****"
        var bytesRead: Int
        var bytesAvailable: Int
        var bufferSize: Int
        val buffer: ByteArray
        val maxBufferSize = 1 * 1024 * 1024
        val sourceFile = File(sourceFileUri)

        return if (!sourceFile.isFile) {
            WeiboDialogUtils.closeDialog(loadingDialog)
            runOnUiThread(Runnable {
                Toast.makeText(applicationContext, "Source File not exist :$imagepath", Toast.LENGTH_SHORT).show()
            })

            return  0
        } else {
            try {
                // open a URL connection to the Servlet
                val fileInputStream = FileInputStream(sourceFile)
                val url = URL(upLoadServerUri)
                conn = url.openConnection() as HttpURLConnection
                conn.doInput = true // Allow Inputs
                conn.doOutput = true // Allow Outputs
                conn.useCaches = false // Don't use a Cached Copy
                conn.requestMethod = "POST"
                conn.setRequestProperty("Connection", "Keep-Alive")
                conn.setRequestProperty("ENCTYPE", "multipart/form-data")
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
                conn.setRequestProperty("uploaded_file", fileName)
                dos = DataOutputStream(conn.outputStream)
                dos.writeBytes(twoHyphens + boundary + lineEnd)
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd)
                dos.writeBytes(lineEnd)

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available()
                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                buffer = ByteArray(bufferSize)

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize)

                while (bytesRead > 0) {
                  dos.write(buffer, 0, bufferSize)
                  bytesAvailable = fileInputStream.available()
                  bufferSize = Math.min(bytesAvailable, maxBufferSize)
                  bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                }

                dos.writeBytes(lineEnd)
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
                serverResponseCode = conn.responseCode
                val serverResponseMessage = conn.responseMessage
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode)

                if (serverResponseCode == 200) {
                   // WeiboDialogUtils.closeDialog(loadingDialog)
                    runOnUiThread(Runnable {  Toast.makeText(applicationContext, "File Upload Complete."+uploadPhotoNamewithoutExtention, Toast.LENGTH_SHORT).show()})
                    type?.let { sendMedia(uploadPhotoName, it) }
                }

                fileInputStream.close()
                dos.flush()
                dos.close()
            } catch (ex: MalformedURLException) {
                WeiboDialogUtils.closeDialog(loadingDialog)
                ex.printStackTrace()
               runOnUiThread(Runnable { Toast.makeText(applicationContext, "MalformedURLException", Toast.LENGTH_SHORT).show() })
            } catch (e: java.lang.Exception) {
                WeiboDialogUtils.closeDialog(loadingDialog)
                e.printStackTrace()
                runOnUiThread(Runnable { Toast.makeText(applicationContext, "Upload fault", Toast.LENGTH_SHORT).show() })
            }

            return serverResponseCode
        }
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = currentFocus
        if (view == null) {
          view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
