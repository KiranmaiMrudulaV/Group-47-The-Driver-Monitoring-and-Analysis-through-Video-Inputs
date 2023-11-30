package com.cse535.cse535_project4_ui

import android.os.Bundle
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.ComponentActivity

class DriverResultActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_result_activity)

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