package com.cse535.cse535_project4_ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import com.google.firebase.database.*
import okhttp3.MediaType
import java.util.UUID

interface ApiService {

    @Multipart
    @POST
    fun uploadImage(
        @Url url: String,
        @Part image: MultipartBody.Part,
        @Part("id") id: RequestBody
    ): Call<ResponseBody>
}

class MainActivity : ComponentActivity() {

    private val REQUEST_VIDEO_CAPTURE = 1
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    public val responseMap: HashMap<String, MutableList<String>> = HashMap()
//    public val uuidList = mutableListOf<String>()

    companion object {
        val uuidList = mutableListOf<String>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton: Button = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            checkCameraPermissionAndStartVideoCapture()
        }

        val myButton: Button = findViewById(R.id.respButton)
        val myHearRateButton: Button = findViewById(R.id.buttonHeartbeat)
        val myRespTextView : TextView = findViewById(R.id.textViewRespReading)
        val myHBTextView : TextView = findViewById(R.id.textViewHeartBeatReading)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val textViewLoading: TextView = findViewById(R.id.textViewLoading)
        var heartBeatResAvailable = false;
        var respResAvailable = false;

        var respReadingInt = 0;
        var heartBeatReadingInt = 0;
        myButton.setOnClickListener {
            myRespTextView.text = "Respiratory Rate is: "
            respReadingInt = loadCSVData()
            myRespTextView.text = myRespTextView.text as String + respReadingInt.toString()
            respResAvailable = true;
            // Remove this comment:
            if (respResAvailable && heartBeatResAvailable)
                startButton.isEnabled = true;
        }

        myHearRateButton.setOnClickListener {
            myHBTextView.text = "Heart Rate is: "
            val fd = resources.openRawResourceFd(R.raw.heartbeat)
            progressBar.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            textViewLoading.visibility = View.VISIBLE
            textViewLoading.text = "Processing the Video..."
            doInBackground(fd) { result ->
                // Update UI or use result
                if (result != null) {
                    heartBeatReadingInt = result.toInt()
                }
                myHBTextView.text = myHBTextView.text as String + result.toString()
                heartBeatResAvailable = true;
                progressBar.visibility = View.GONE
                textViewLoading.visibility = View.GONE
                textViewLoading.text = ""
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                if (respResAvailable && heartBeatResAvailable)
                    startButton.isEnabled = true;
            }
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("/")

        val keys = listOf("Distraction", "Emotion", "Workload")
        for (key in keys) {
            responseMap[key] = mutableListOf()
        }

        // Add a ValueEventListener to continuously listen for changes
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method will be called whenever data at the specified location changes
                // dataSnapshot contains the current data at the specified database location
                for (recordSnapshot in dataSnapshot.children) {
                    val record = recordSnapshot.getValue(FirebaseRecordClass::class.java)
                    // Process the record as needed

                    if (record != null) {
                        if (record.UUID in uuidList) {
                            val list = responseMap["Distraction"]
                            record.distraction?.let { list?.add(it) }

                            val list1 = responseMap["Emotion"]
                            record.emotion?.let { list1?.add(it) }
                            val list2 = responseMap["Workload"]
                            record.workload?.let { list2?.add(it) }

                            DataHolder.responseMap.postValue(responseMap)
                            uuidList.remove(record.UUID)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors or cleanup operations
            }
        })

    }

    object ApiClient {

        private const val BASE_URL = "http://13.57.240.107/"

        private var retrofit: Retrofit? = null

//        public val uuidList = mutableListOf<String>()

        private fun getRetrofitInstance(): Retrofit {
            if (retrofit == null) {
                val httpClient = OkHttpClient.Builder()

                // Add logging interceptor for debugging purposes
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                httpClient.addInterceptor(logging)

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .build()
            }
            return retrofit!!
        }

        fun generateRandomUUID(): String {
            return UUID.randomUUID().toString()
        }

        fun uploadImage(bitmap: Bitmap) {
            // Convert Bitmap to byte array
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // Create RequestBody from the encoded image
            val imageBody = RequestBody.create("text/plain".toMediaTypeOrNull(), encodedImage)

            // Create MultipartBody.Part using the image request body
            val body = MultipartBody.Part.createFormData("image", "image.png", imageBody)

            // Create API service instance
            val apiService: ApiService = getRetrofitInstance().create(ApiService::class.java)

            val genratedUUID = generateRandomUUID();
            MainActivity.uuidList.add(genratedUUID)
            // Make the API call
            val callDistraction: Call<ResponseBody> = apiService.uploadImage(BASE_URL + "distraction", body, RequestBody.create(
                "text/plain".toMediaTypeOrNull(), genratedUUID))
            callDistraction.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // Handle success
                        // Do something with the response
                        val responseBody = response.body()?.string()
                        if (!responseBody.isNullOrEmpty()) {
                            // Parse the JSON using Gson
                            val gson = Gson()
                            val jsonElement: JsonElement = gson.fromJson(responseBody, JsonElement::class.java)

                            // Now you can work with the JsonElement
                            if (jsonElement.isJsonObject) {
                                val jsonObject = jsonElement.asJsonObject
                                // Access JSON fields using jsonObject.get("fieldName")
                                // Example: val value = jsonObject.get("key1").asString
//                                val isDriverDistracted = jsonObject.get("prediction").asString
//                                val list = responseMap["Distraction"]
//                                list?.add(isDriverDistracted)
//                                DataHolder.responseMap.postValue(responseMap)
                            } else if (jsonElement.isJsonArray) {
                                val jsonArray = jsonElement.asJsonArray
                                // Access elements in the JSON array
                            }
                        }
                    } else {
                        // Handle error
                        // Log error or show an error message
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Handle failure
                    // Log error or show an error message
                }
            })

            val callEmotion: Call<ResponseBody> = apiService.uploadImage(BASE_URL + "emotion", body, RequestBody.create(
                "text/plain".toMediaTypeOrNull(), genratedUUID))
            callEmotion.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // Handle success
                        // Do something with the response
                        val responseBody = response.body()?.string()
                        if (!responseBody.isNullOrEmpty()) {
                            // Parse the JSON using Gson
                            val gson = Gson()
                            val jsonElement: JsonElement = gson.fromJson(responseBody, JsonElement::class.java)

                            // Now you can work with the JsonElement
                            if (jsonElement.isJsonObject) {
                                val jsonObject = jsonElement.asJsonObject
                                // Access JSON fields using jsonObject.get("fieldName")
                                // Example: val value = jsonObject.get("key1").asString
//                                val isDriverDistracted = jsonObject.get("prediction").asString
//                                val list = responseMap["Emotion"]
//                                list?.add(isDriverDistracted)
//                                DataHolder.responseMap.postValue(responseMap)
                            } else if (jsonElement.isJsonArray) {
                                val jsonArray = jsonElement.asJsonArray
                                // Access elements in the JSON array
                            }
                        }
                    } else {
                        // Handle error
                        // Log error or show an error message
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Handle failure
                    // Log error or show an error message
                }
            })

            val callWorkload: Call<ResponseBody> = apiService.uploadImage(BASE_URL + "workload", body, RequestBody.create(
                "text/plain".toMediaTypeOrNull(), genratedUUID))
            callWorkload.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // Handle success
                        // Do something with the response
                        val responseBody = response.body()?.string()
                        if (!responseBody.isNullOrEmpty()) {
                            // Parse the JSON using Gson
                            val gson = Gson()
                            val jsonElement: JsonElement = gson.fromJson(responseBody, JsonElement::class.java)

                            // Now you can work with the JsonElement
                            if (jsonElement.isJsonObject) {
                                val jsonObject = jsonElement.asJsonObject
                                // Access JSON fields using jsonObject.get("fieldName")
                                // Example: val value = jsonObject.get("key1").asString
//                                val isDriverDistracted = jsonObject.get("prediction").asString
//                                val list = responseMap["Workload"]
//                                list?.add(isDriverDistracted)
//                                DataHolder.responseMap.postValue(responseMap)
                            } else if (jsonElement.isJsonArray) {
                                val jsonArray = jsonElement.asJsonArray
                                // Access elements in the JSON array
                            }
                        }
                    } else {
                        // Handle error
                        // Log error or show an error message
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // Handle failure
                    // Log error or show an error message
                }
            })



        }
    }

    // Respiratory Reading Code:
    private val accelValuesX = mutableListOf<Double>()
    private val accelValuesY = mutableListOf<Double>()
    private val accelValuesZ = mutableListOf<Double>()

    private var mediaFileName = ""

    private fun loadCSVData(): Int {
        val inputStream: InputStream = resources.openRawResource(R.raw.csvbreathe27v1)
        val reader = BufferedReader(InputStreamReader(inputStream))

        for (i in 0 until 11) {
            reader.readLine()
        }

        for (i in 0..1278) {
            val line = reader.readLine()
            if (line != null) {
                try {
                    accelValuesZ.add(line.toDouble())
                } catch (e: NumberFormatException) {
                    // Handle malformed data or add logging here
                }
            }
        }

        for (i in 1283..2559) {
            val line = reader.readLine()
            if (line != null) {
                try {
                    accelValuesY.add(line.toDouble())
                } catch (e: NumberFormatException) {
                    // Handle malformed data or add logging here
                }
            }
        }

        for (i in 2563..3838) {
            val line = reader.readLine()
            if (line != null) {
                try {
                    accelValuesX.add(line.toDouble())
                } catch (e: NumberFormatException) {
                    // Handle malformed data or add logging here
                }
            }
        }

        var ret = callRespiratoryCalculator()

        inputStream.close()
        Log.d("MainActivity", ret.toString())
        return ret;
    }

    private fun callRespiratoryCalculator():Int {
        var previousValue = 0f
        var currentValue = 0f
        previousValue = 10f
        var k=0
        var sum = 0f;
        var n = 0;
        for (i in 11..450) {
            currentValue = sqrt(
                Math.pow(accelValuesZ[i].toDouble(), 2.0) +
                        Math.pow(
                            accelValuesX[i].toDouble(),
                            2.0
                        )
                        + Math.pow(accelValuesY[i].toDouble(), 2.0)
            ).toFloat()
            Log.d("MainActivity", "diff x is: " + abs(previousValue - currentValue))

            sum += abs(previousValue - currentValue)
            n++
            if (abs(x = previousValue - currentValue) > 0.04) {
                k++
            }

            previousValue=currentValue
        }
        Log.d("MainActivity", "avg is: " + sum/n)

        val ret= (k/45.00)
        Log.d("MainActivity", "k is: $k")
        Log.d("MainActivity", "ret is: $ret")

        return (ret*30).toInt()
    }


    // Hearbeat Reading Code:
    fun doInBackground(fd: AssetFileDescriptor, callback: (String?) -> Unit) {
        Log.d("Pratik", "doInBckgrnd fn is started!")
        CoroutineScope(Dispatchers.Main).launch { // or CoroutineScope(Dispatchers.Main).launch if not in ViewModel
            val result = withContext(Dispatchers.IO) {
                val retriever = MediaMetadataRetriever()
                val frameList = ArrayList<Bitmap>()
                try {
                    retriever.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                    val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)
                    val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                    val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    val aduration = duration!!.toInt()
                    Log.d("Pratik" , "duration: $duration, aduration: $aduration , width: $width, height: $height")
                    var i = 10
                    while (i < aduration) {
                        val bitmap = retriever.getFrameAtIndex(i)
                        frameList.add(bitmap!!)
                        i += 5
                    }
                } catch (m_e: Exception) {
                    // Handle exception
                    Log.d("Pratik", m_e.toString())
                } finally {
                    retriever.release()
                }

//                retriever?.release()
                var redBucket: Long = 0
                var pixelCount: Long = 0
                val a = mutableListOf<Long>()
                for (i in frameList) {
                    redBucket = 0
                    for (y in 150 until 250) {
                        for (x in 50 until 150) {
                            val c: Int = i.getPixel(x, y)
                            pixelCount++
                            redBucket += Color.red(c) + Color.blue(c) +
                                    Color.green(c)
                        }
                    }
                    a.add(redBucket)
                }
                val b = mutableListOf<Long>()

                Log.d("Pratik" , "frameList size: " + frameList.size.toString())
                Log.d("Pratik" , "a size: " + a.size.toString())
                Log.d("Pratik" , "a: " + a.toString())


                for (i in 0 until a.lastIndex - 5) {
                    var temp =
                        (a.elementAt(i) + a.elementAt(i + 1) + a.elementAt(i + 2)
                                + a.elementAt(
                            i + 3
                        ) + a.elementAt(
                            i + 4
                        )) / 4
                    b.add(temp)
                }
                Log.d("Pratik" , "b: " + b.toString())

                var x = b.elementAt(0)
                var count = 0
                for (i in 1 until b.lastIndex) {
                    var p=b.elementAt(i.toInt())
                    if ((p-x) > 3500) {
                        count = count + 1
                    }
                    x = b.elementAt(i.toInt())
                }
                var rate = ((count.toFloat() / 45) * 60).toInt()

                return@withContext (rate / 2).toString()
            }

            callback(result)
        }
    }

    private fun checkCameraPermissionAndStartVideoCapture() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is granted, start video capture
            dispatchTakeVideoIntent()
        }
    }

    private fun dispatchTakeVideoIntent() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri())
        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
    }

    private fun getOutputMediaFile(): File {
        // Get the external storage directory (Movies directory in this case)
        val mediaDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            "CapturedVideos"
        ).apply { mkdirs() }

        // Create a unique file name based on the current timestamp
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaFile = File(mediaDir, "VID_${timeStamp}.mp4")
        mediaFileName = "VID_${timeStamp}.mp4"

        return mediaFile
    }


    private fun getOutputMediaFileUri(): Uri {
        val file = getOutputMediaFile()
        return FileProvider.getUriForFile(
            this,
            "com.cse535.cse535_project4_ui",
            file
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start video capture
                dispatchTakeVideoIntent()
            } else {
                // Permission denied, show a message or handle accordingly
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            // Handle the recorded video, e.g., save it or play it
//            val videoFilePath = getCapturedVideoPath(data)  // Replace with the actual path of your video file
            val videoFilePath = "/storage/emulated/0/Movies/CapturedVideos/$mediaFileName"
            val apiUrl = "http://13.57.240.107/distraction"
            val frames = extractFrames(videoFilePath, 2)

            // Upload each frame to the API
            var responseMapReturned : HashMap<String, MutableList<String>> = HashMap()
            frames.forEach { frame ->
                ApiClient.uploadImage(frame)
            }

            // Spawn New Intent:
            val driverActivityIntent = Intent(this@MainActivity, DriverResultActivity::class.java)
            driverActivityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            driverActivityIntent.putExtra("dataMap", responseMapReturned)
            startActivity(driverActivityIntent)

        } else {
            // Handle the case where the captured video file is null
            Toast.makeText(this, "Error capturing video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractFrames(videoPath: String, intervalInSeconds: Int): List<Bitmap> {
        val frames = mutableListOf<Bitmap>()

        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoPath)

            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
            val intervalUs = intervalInSeconds * 1000000L

            for (timeUs in 0 until duration * 1000 step intervalUs) {
                val frame = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                if (frame != null) {
                    frames.add(frame)
                }
            }

            retriever.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return frames
    }

    private fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, size)
    }

//    private fun getCapturedVideoPath(data: Intent?): String? {
//        // Extract the file URI from the intent
//        val videoUri: Uri? = data?.data
//
//        // Convert the URI to a File
//        if (videoUri != null) {
//            return getRealPathFromURI(videoUri)
//        }
//        return null
//    }
//
//    private fun getRealPathFromURI(uri: Uri): String {
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(uri, projection, null, null, null)
//        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        cursor?.moveToFirst()
//        val path = columnIndex?.let { cursor?.getString(it) }
//        cursor?.close()
//        return path
//    }

}