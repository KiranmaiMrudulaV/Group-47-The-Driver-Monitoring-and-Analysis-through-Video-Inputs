package com.cse535.cse535_project4_ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.ComponentActivity

class DriverResultActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_result_activity)

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)

        val driverResultTextView : TextView = findViewById(R.id.distractionStatusTextView)
        val tableLayout = findViewById<TableLayout>(R.id.driverResultTable)

//        val dataMap = intent.getSerializableExtra("dataMap") as? HashMap<String, MutableList<String>>
        DataHolder.responseMap.observe(this) { dataMap ->
            // Handle updated responseMap
            // This code will be called whenever the responseMap is updated

            // Create data rows
            if (dataMap != null) {
                val numRows = dataMap.values.firstOrNull()?.size ?: 0
                for (i in 0 until numRows) {
                    val dataRow = TableRow(this)
                    for ((columnName, values) in dataMap) {
                        val dataTextView = createTextView(values.getOrElse(i) { "" }, false)
                        dataRow.addView(dataTextView)
                    }
                    tableLayout.addView(dataRow)
                }
            }

            val valuesForKey = dataMap["Distraction"]
            if (valuesForKey != null && valuesForKey.contains("distracted")) {
                soundAlarm();
            }
        }

        // Create headers row
        val headersRow = TableRow(this)
        val headerKeys = listOf("Distraction", "Emotion", "Workload")
        for (key in headerKeys) {
            val headerTextView = createTextView(key, true)
            headersRow.addView(headerTextView)
        }
        tableLayout.addView(headersRow)

    }

    private fun soundAlarm() {
        startAlarm();
        // Dialog:
        val builder = AlertDialog.Builder(this)

        // Set the dialog title and message
        builder.setTitle("Popup Dialog")
            .setMessage("Warning! You appear to be sleepy. Be attentive.")

        // Add a button to close the dialog
        builder.setPositiveButton("Close") { dialog: DialogInterface, _: Int ->
            // You can perform actions here if needed
            dialog.dismiss()
            stopAlarm()
        }

        // Create and show the dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Function to start the alarm sound
    private fun startAlarm() {
        mediaPlayer?.start()
    }

    // Function to stop the alarm sound
    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.prepare()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer when the activity is destroyed
        mediaPlayer?.release()
    }

    private fun createTextView(text: String, isHeader: Boolean): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.gravity = Gravity.CENTER
        textView.setPadding(8, 8, 8, 8)

        if (isHeader) {
            textView.setBackgroundResource(R.drawable.table_header_background) // Customize header background if needed
            textView.setTextColor(resources.getColor(R.color.colorHeaderText)) // Customize header text color if needed

        } else {
            textView.setBackgroundResource(R.drawable.table_data_background) // Customize data background if needed
            textView.setTextColor(resources.getColor(R.color.colorDataText)) // Customize data text color if needed
        }

        return textView
    }

}