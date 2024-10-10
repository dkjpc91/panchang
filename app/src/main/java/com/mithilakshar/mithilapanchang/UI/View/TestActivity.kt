package com.mithilakshar.mithilapanchang.UI.View

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.Adapters.caladapter
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Utility.CalendarHelper
import com.mithilakshar.mithilapanchang.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: caladapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbName = "cal.db"


// Create an instance of CalendarHelper
        val calendarHelper = CalendarHelper(this, dbName)

        val tableName = "cal" // Replace with your actual table name
     //   val allTableData = calendarHelper.getAllTableData(tableName)
        val allTableDatamonth = calendarHelper.getAllTableDataForMonth(tableName,"DECEMBER")



        if (allTableDatamonth.isNotEmpty()) {
            for ((index, row) in allTableDatamonth.withIndex()) {
                Log.d("TestActivity", "Row $index: $row")
            }
        } else {
            Log.d("TestActivity", "No data found for month: DECEMBER")
        }

        // Don't forget to close the database when done
        calendarHelper.closeDatabase()

        val mergedRows = mergeRowsByDate(allTableDatamonth)
        logMergedRows(mergedRows)

        recyclerView = binding.calrecycler
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = caladapter(this,mergedRows)
        recyclerView.adapter = adapter

    }



    fun mergeRowsByDate(rows: List<Map<String, Any?>>): List<Map<String, Any?>> {
        val mergedMap = mutableMapOf<Any?, MutableMap<String, Any?>>()

        for (row in rows) {
            val dateKey = row["date"] // Use "date" as the unique key for merging
            if (!mergedMap.containsKey(dateKey)) {
                mergedMap[dateKey] = mutableMapOf()
            }
            val existingRow = mergedMap[dateKey]!!

            for ((k, v) in row) {
                if (existingRow.containsKey(k)) {
                    // If the value is different, merge them into a list
                    if (existingRow[k] != v) {
                        existingRow[k] = if (existingRow[k] is List<*>) {
                            (existingRow[k] as List<Any?>) + v
                        } else {
                            listOf(existingRow[k], v)
                        }
                    }
                } else {
                    existingRow[k] = v
                }
            }
        }

        return mergedMap.values.map { it.toMap() }
    }



    fun logMergedRows(mergedRows: List<Map<String, Any?>>) {
        mergedRows.forEachIndexed { index, row ->
            val logMessage = StringBuilder("Row ${index + 1}: ")
            row.entries.forEach { entry ->
                logMessage.append("${entry.key}=${entry.value}, ")
            }
            // Remove the last comma and space
            if (logMessage.isNotEmpty()) {
                logMessage.setLength(logMessage.length - 2)
            }
            // Use Log.d to log the message
            Log.d("MergedRow", logMessage.toString())
        }
}

}