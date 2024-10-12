package com.mithilakshar.mithilapanchang.Utility

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File

class CalendarHelper(context: Context, dbName: String) {

    private val TAG = "CalendarHelper"
    private val dbFolderPath = context.getExternalFilesDir(null)?.absolutePath + File.separator + "test"
    private val dbFilePath = "$dbFolderPath/$dbName"
    private var db: SQLiteDatabase? = null

    init {
        openDatabase()
    }

    /**
     * Opens the database for reading and writing.
     */
    private fun openDatabase() {
        try {
            db = SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READWRITE)
            Log.d(TAG, "Database opened: $dbFilePath")
        } catch (e: Exception) {
            Log.e(TAG, "Error opening database", e)
        }
    }

    /**
     * Fetches the calendar data for a specific month, combining multiple `tithi`, `nakshatra`, and `yog` values.
     */



    fun getAllTableDataForMonth(tableName: String, monthName: String): List<Map<String, Any?>> {
        val dataList = mutableListOf<Map<String, Any?>>()

        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for table: $tableName")
                return emptyList()
            }

            // Update the query to filter by month name
            val cursor = database.rawQuery("SELECT * FROM $tableName WHERE month = ?", arrayOf(monthName))
            cursor.use { c ->
                val columnNames = getColumnNames(tableName)  // Reuse existing function for column names
                var lastRow: MutableMap<String, Any?>? = null

                while (c.moveToNext()) {
                    val rowData = mutableMapOf<String, Any?>()
                    for (i in columnNames.indices) {
                        val columnName = columnNames[i]
                        val value = c.getString(i)  // Assuming all columns are string for simplicity
                        rowData[columnName] = value?.takeIf { it.isNotBlank() }  // Keep only non-blank values
                    }

                    // Check if the current row is blank in critical fields (e.g., date)
                    if (rowData["date"] == null) {
                        // If the current row is blank, merge with the last row
                        if (lastRow != null) {
                            // Merge values from the lastRow into rowData where applicable
                            for (column in columnNames) {
                                if (rowData[column] == null) {
                                    rowData[column] = lastRow[column]
                                }
                            }
                        }
                    } else {
                        // If it's a valid row, store it as lastRow
                        lastRow = rowData
                    }

                    // Only add the row if it has meaningful data
                    if (rowData["date"] != null || lastRow != null) {
                        dataList.add(rowData)
                    }
                }
            }
        }
        return dataList
    }

    /**
     * Retrieves the column names for a specified table.
     */

    @SuppressLint("Range")
    fun getRowByMonthAndDate(month: String, date: String,table: String): Map<String, String>? {
        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading rows by month: $month and date: $date")
                return null
            }

            val query = "SELECT * FROM $table WHERE month = ? AND date = ?"
            val selectionArgs = arrayOf(month, date)

            database.rawQuery(query, selectionArgs)?.use { cursor ->
                val columnNames = cursor.columnNames

                if (cursor.moveToFirst()) {
                    val rowData = mutableMapOf<String, String>()
                    for (columnName in columnNames) {
                        val value = cursor.getString(cursor.getColumnIndex(columnName)) ?: ""
                        rowData[columnName] = value
                    }
                    return rowData
                }
            }
        } ?: Log.e(TAG, "Database is null!")

        return null
    }



    fun getAllTableData(tableName: String): List<Map<String, Any?>> {
        val dataList = mutableListOf<Map<String, Any?>>()

        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for table: $tableName")
                return emptyList()
            }

            val cursor = database.rawQuery("SELECT * FROM $tableName", null)
            cursor.use { c ->
                val columnNames = getColumnNames(tableName)  // Reuse existing function for column names
                var lastRow: MutableMap<String, Any?>? = null

                while (c.moveToNext()) {
                    val rowData = mutableMapOf<String, Any?>()
                    for (i in columnNames.indices) {
                        val columnName = columnNames[i]
                        val value = c.getString(i)  // Assuming all columns are string for simplicity
                        rowData[columnName] = value?.takeIf { it.isNotBlank() }  // Keep only non-blank values
                    }

                    // Check if the current row is blank in critical fields (e.g., date)
                    if (rowData["date"] == null) {
                        // If the current row is blank, merge with the last row
                        if (lastRow != null) {
                            // Merge values from the lastRow into rowData where applicable
                            for (column in columnNames) {
                                if (rowData[column] == null) {
                                    rowData[column] = lastRow[column]
                                }
                            }
                        }
                    } else {
                        // If it's a valid row, store it as lastRow
                        lastRow = rowData
                    }

                    // Only add the row if it has meaningful data
                    if (rowData["date"] != null || lastRow != null) {
                        dataList.add(rowData)
                    }
                }
            }
        }
        return dataList
    }

    /**
     * Retrieves the column names for a specified table.
     */
    private fun getColumnNames(tableName: String): List<String> {
        val columnNames = mutableListOf<String>()
        db?.let { database ->
            val cursor = database.rawQuery("SELECT * FROM $tableName LIMIT 1", null)
            val columnCount = cursor.columnCount
            for (i in 0 until columnCount) {
                columnNames.add(cursor.getColumnName(i))
            }
            cursor.close()
        }
        return columnNames
    }


    fun closeDatabase() {
        db?.close()
        db = null
        Log.d(TAG, "Database closed.")
    }
}

/**
 * Data class to represent a calendar entry.
 */
data class CalendarEntry(
    val date: Int,
    var day: String,
    var tithi: String,
    var tithiEnd: String,
    var nakshatra: String,
    var nakshatraEnd: String,
    var yog: String,
    var yogEnd: String
)