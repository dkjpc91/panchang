package com.mithilakshar.mithilapanchang.Utility

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File


class dbHelper(context: Context, dbName: String) {

    private val TAG = "DBHelper"
    val dbFolderPath = context.getExternalFilesDir(null)?.absolutePath + File.separator + "test"
    val dbFilePath = "$dbFolderPath/$dbName"
    private var db: SQLiteDatabase? = null

    init {
        try {
            db = SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READWRITE)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening database", e)
        }
    }

    fun getTableNames(): List<String> {
        val tableNames = mutableListOf<String>()
        db?.let {
            val cursor = it.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name != 'android_metadata'", null)
            try {
                cursor.use { c ->
                    while (c.moveToNext()) {
                        val tableName = c.getString(0)
                        tableNames.add(tableName)
                    }
                }
            } finally {
                cursor.close()  // Close the cursor even if an exception occurs
            }
        }
        return tableNames
    }


    @SuppressLint("Range")
    fun getColumnNames(tableName: String): List<String> {
        val columnNames = mutableListOf<String>()
        try {
            db?.let { db ->
                if (!db.isOpen) {
                    // Handle case where database is not open
                    return emptyList()
                }
                val cursor = db.rawQuery("PRAGMA table_info($tableName)", null)

                cursor.use { c ->
                    while (c.moveToNext()) {
                        val columnName = c.getString(c.getColumnIndex("name"))
                        columnNames.add(columnName)
                        Log.d(TAG, "Column Name: $columnName")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching column names", e)
            // Handle exception (e.g., throw or return empty list)
        } finally {
            //db?.close()
        }
        return columnNames
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
                while (c.moveToNext()) {
                    val rowData = mutableMapOf<String, Any?>()
                    for (i in 0 until columnNames.size) {
                        val columnName = columnNames[i]
                        val value = c.getString(i)  // Assuming all columns are string for simplicity
                        rowData[columnName] = value
                    }
                    dataList.add(rowData)
                }
            }
        }
        return dataList
    }


    fun getRowCount(tableName: String): Int {
        var rowCount = 0
        db?.let {
            val cursor = it.rawQuery("SELECT COUNT(*) FROM $tableName", null)
            cursor.use { c ->
                if (c.moveToFirst()) {
                    rowCount = c.getInt(0)
                }
            }
        }
        return rowCount
    }

    fun getRowValues(tableName: String, primaryKeyValue: Any): List<Any>? {
        var rowValues: MutableList<Any>? = null // Use MutableList instead of List
        db?.let {
            val cursor = it.rawQuery("SELECT * FROM $tableName WHERE id = ?", arrayOf(primaryKeyValue.toString()))
            cursor.use { c ->
                if (c.moveToFirst()) {
                    rowValues = mutableListOf()
                    do {
                        for (i in 0 until c.columnCount) {
                            val value = when (c.getType(i)) {
                                Cursor.FIELD_TYPE_NULL -> null
                                Cursor.FIELD_TYPE_INTEGER -> c.getLong(i)
                                Cursor.FIELD_TYPE_FLOAT -> c.getDouble(i)
                                Cursor.FIELD_TYPE_STRING -> c.getString(i)
                                Cursor.FIELD_TYPE_BLOB -> c.getBlob(i)
                                else -> null
                            }
                            rowValues!!.add(value ?: "") // Add value to the mutable list
                        }
                    } while (c.moveToNext())
                }
            }
        }
        return rowValues
    }


    @SuppressLint("Range")
    fun doesColumnExist(tableName: String, columnName: String): Boolean {
        val query = "PRAGMA table_info($tableName)"
        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for checking column existence.")
                return false
            }

            database.rawQuery(query, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val column = cursor.getString(cursor.getColumnIndex("name"))
                    if (column == columnName) {
                        return true
                    }
                }
            }
        }
        return false
    }

    @SuppressLint("Range")
    fun getHolidaysByMonthanddb(monthName: String,dbName: String): List<Map<String, String>> {
        val holidays = mutableListOf<Map<String, String>>()
        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading holidays for month: $monthName")
                return emptyList()
            }

            val query = "SELECT * FROM $dbName WHERE month = ?"
            val selectionArgs = arrayOf(monthName)

            database.rawQuery(query, selectionArgs)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val rowData = mutableMapOf<String, String>()
                    val month = cursor.getString(cursor.getColumnIndex("month"))
                    val value1 = cursor.getString(cursor.getColumnIndex("date"))
                    val value2 = cursor.getString(cursor.getColumnIndex("name"))
                    val value3 = cursor.getString(cursor.getColumnIndex("desc"))
                    rowData["month"] = month
                    rowData["date"] = value1
                    rowData["name"] = value2
                    rowData["desc"] = value3
                    holidays.add(rowData)
                }
            }
        }

        if (holidays.isEmpty()) {
            // Example: Return a default value indicating no holidays found
            val defaultHoliday = mutableMapOf<String, String>()
            defaultHoliday["month"] = monthName
            defaultHoliday["date"] = ""
            defaultHoliday["name"] = "अपडेट प्रक्रिया में"
            holidays.add(defaultHoliday)
        }

        return holidays
    }


    @SuppressLint("Range")
    fun searchFilter(searchText: String, tableName: String): List<Map<String, String>> {
        val filteredResults = mutableListOf<Map<String, String>>()
        val TAG = "searchtable"

        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for searching with filter: $searchText")
                return emptyList()
            }

            Log.d(TAG, "Searching in table: $tableName for text: $searchText")

            val query = "SELECT * FROM $tableName WHERE filter LIKE ?"
            val selectionArgs = arrayOf("%$searchText%")

            Log.d(TAG, "Executing query: $query with args: ${selectionArgs.joinToString()}")

            database.rawQuery(query, selectionArgs)?.use { cursor ->
                val rowCount = cursor.count
                Log.d(TAG, "Number of rows found: $rowCount")

                while (cursor.moveToNext()) {
                    val rowData = mutableMapOf<String, String>()

                    val month = cursor.getString(cursor.getColumnIndex("month"))
                    val value1 = cursor.getString(cursor.getColumnIndex("date"))
                    val value2 = cursor.getString(cursor.getColumnIndex("name"))
                    val value3 = cursor.getString(cursor.getColumnIndex("desc"))

                    Log.d(TAG, "Retrieved row - Month: $month, Date: $value1, Name: $value2, Desc: $value3")

                    rowData["month"] = month
                    rowData["date"] = value1
                    rowData["name"] = value2
                    rowData["desc"] = value3

                    filteredResults.add(rowData)
                }
            }
        } ?: Log.e(TAG, "Database reference is null")

        Log.d(TAG, "Filtered results: $filteredResults")
        return filteredResults
    }



    @SuppressLint("Range")
    fun getHolidaysByMonth(monthName: String,dbtableName: String): List<Map<String, String>> {
        val holidays = mutableListOf<Map<String, String>>()
        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading holidays for month: $monthName")
                return emptyList()
            }

            val query = "SELECT * FROM $dbtableName WHERE month = ?"
            val selectionArgs = arrayOf(monthName)

            database.rawQuery(query, selectionArgs)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val rowData = mutableMapOf<String, String>()
                    val month = cursor.getString(cursor.getColumnIndex("month"))
                    val value1 = cursor.getString(cursor.getColumnIndex("date"))
                    val value2 = cursor.getString(cursor.getColumnIndex("name"))
                    val value3 = cursor.getString(cursor.getColumnIndex("desc"))
                    rowData["month"] = month
                    rowData["date"] = value1
                    rowData["name"] = value2
                    rowData["desc"] = value3
                    holidays.add(rowData)
                }
            }
        }

        if (holidays.isEmpty()) {
            // Example: Return a default value indicating no holidays found
            val defaultHoliday = mutableMapOf<String, String>()
            defaultHoliday["month"] = monthName
            defaultHoliday["date"] = ""
            defaultHoliday["name"] = "अपडेट प्रक्रिया में।"
            holidays.add(defaultHoliday)
        }

        return holidays
    }


    @SuppressLint("Range")
    fun getHolidaysByMonthdate(monthName: String, startDate: String, dbtableName: String): List<Map<String, String>> {
        val holidays = mutableListOf<Map<String, String>>()

        // Validate parameters
        if (monthName.isEmpty() || startDate.isEmpty()) {
            Log.w(TAG, "Invalid parameters: monthName or startDate is empty")
            return holidays
        }

        // Ensure startDate is formatted correctly
        val startDay = startDate.toIntOrNull() // Convert to an integer for comparison
        if (startDay == null) {
            Log.w(TAG, "Invalid startDate: $startDate is not a valid number")
            return holidays
        }

        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading holidays for month: $monthName")
                return holidays
            }

            // SQL query to select holidays for the given month and datenumber greater than startDate
            val query = "SELECT * FROM $dbtableName WHERE month = ? AND CAST(datenumber AS INTEGER) > ?"
            val selectionArgs = arrayOf(monthName, startDay.toString()) // Convert back to string for SQL query

            try {
                database.rawQuery(query, selectionArgs)?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val rowData = mutableMapOf<String, String>()
                        rowData["month"] = cursor.getString(cursor.getColumnIndex("month"))
                        rowData["date"] = cursor.getString(cursor.getColumnIndex("date")) // Assuming this is also relevant
                        rowData["name"] = cursor.getString(cursor.getColumnIndex("name"))
                        rowData["desc"] = cursor.getString(cursor.getColumnIndex("desc"))
                        holidays.add(rowData)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error querying holidays: ${e.message}", e)
            }
        }

        // Handle empty result set
        if (holidays.isEmpty()) {
            val defaultHoliday = mutableMapOf<String, String>().apply {
                put("month", monthName)
                put("date", "")
                put("name", "मअपडेट प्रक्रिया में।") // Indicates "In the update process."
            }
            holidays.add(defaultHoliday) // Optional: add a default entry if needed
        }

        return holidays
    }








    //get row by getRowsByColumnKeywordIfExists

    @SuppressLint("Range")
    fun getRowsByColumnKeywordIfExists(tableName: String, columnName: String, keyword: String): List<Map<String, Any?>> {
        val rows = mutableListOf<Map<String, Any?>>()

        // Step 1: Check if column exists
        val TAG_COLUMN_CHECK = "ColumnCheck"
        Log.d(TAG_COLUMN_CHECK, "Checking if column $columnName exists in table $tableName")

        if (!doesColumnExist(tableName, columnName)) {
            Log.e(TAG_COLUMN_CHECK, "Column $columnName does not exist in table $tableName.")
            return emptyList()  // Return an empty list if the column doesn't exist
        }

        Log.d(TAG_COLUMN_CHECK, "Column $columnName exists. Proceeding with query.")

        db?.let { database ->
            // Step 2: Database open check
            val TAG_DB_CHECK = "DatabaseCheck"
            if (!database.isOpen) {
                Log.w(TAG_DB_CHECK, "Database not open for reading rows by column keyword")
                return emptyList()
            }
            Log.d(TAG_DB_CHECK, "Database is open. Preparing to run query.")

            // Step 3: Query execution with exact match
            val TAG_QUERY_EXECUTION = "QueryExecution"
            val query = "SELECT * FROM $tableName WHERE $columnName = ?"
            val selectionArgs = arrayOf(keyword)  // No wildcards for exact match
            Log.d(TAG_QUERY_EXECUTION, "Executing query: $query with keyword: $keyword")

            database.rawQuery(query, selectionArgs)?.use { cursor ->
                // Step 4: Fetch column names
                val TAG_COLUMN_FETCH = "ColumnFetch"
                val columnNames = getColumnNames(tableName)
                Log.d(TAG_COLUMN_FETCH, "Fetched column names: $columnNames")

                var rowsFound = false
                while (cursor.moveToNext()) {
                    rowsFound = true
                    val rowData = mutableMapOf<String, Any?>()
                    Log.d(TAG_QUERY_EXECUTION, "Reading new row")

                    // Step 5: Log each column and its value
                    val TAG_ROW_DATA = "RowData"
                    for (name in columnNames) {
                        val value = cursor.getString(cursor.getColumnIndex(name))
                        rowData[name] = value
                        Log.d(TAG_ROW_DATA, "Column: $name, Value: $value")
                    }

                    rows.add(rowData)
                    Log.d(TAG_ROW_DATA, "Row added to the result set: $rowData")
                }

                if (!rowsFound) {
                    Log.d(TAG_QUERY_EXECUTION, "No rows matched the keyword: $keyword")
                }
            }

            Log.d(TAG_QUERY_EXECUTION, "Query execution complete. Number of rows fetched: ${rows.size}")
        }

        return rows
    }






    @SuppressLint("Range")
    fun getRowsByMonth(month: String,table: String): List<Map<String, String>> {
        val rows = mutableListOf<Map<String, String>>()
        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading rows by month: $month")
                return emptyList()
            }

            val query = "SELECT * FROM $table WHERE month = ?"
            val selectionArgs = arrayOf(month)

            database.rawQuery(query, selectionArgs)?.use { cursor ->
                val columnNames = cursor.columnNames

                while (cursor.moveToNext()) {
                    val rowData = mutableMapOf<String, String>()
                    for (columnName in columnNames) {
                        val value = cursor.getString(cursor.getColumnIndex(columnName)) ?: ""
                        rowData[columnName] = value
                    }
                    rows.add(rowData)
                }
            }
        } ?: Log.e(TAG, "Database is null!")

        return rows
    }

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


    @SuppressLint("Range")
    fun getimageByDayName(dayName: String): List<Map<String, String>> {
        val rows = mutableListOf<Map<String, String>>()
        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading rows by day name: $dayName")
                return emptyList()
            }

            val query = "SELECT * FROM iauto WHERE day = ?"
            val selectionArgs = arrayOf(dayName)

            database.rawQuery(query, selectionArgs)?.use { cursor ->
                val columnNames = cursor.columnNames

                while (cursor.moveToNext()) {
                    val rowData = mutableMapOf<String, String>()
                    for (columnName in columnNames) {
                        val value = cursor.getString(cursor.getColumnIndex(columnName)) ?: ""
                        rowData[columnName] = value
                    }
                    rows.add(rowData)
                }
            }
        } ?: Log.e(TAG, "Database is null!")

        return rows
    }



    @SuppressLint("Range")
    fun getimageByholidayname(holidayname: String): List<Map<String, String>> {
        val rows = mutableListOf<Map<String, String>>()
        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading rows by god name: $holidayname")
                return emptyList()
            }

            val query = "SELECT * FROM iauto WHERE holiday = ?"
            val selectionArgs = arrayOf(holidayname)

            database.rawQuery(query, selectionArgs)?.use { cursor ->
                val columnNames = cursor.columnNames

                while (cursor.moveToNext()) {
                    val rowData = mutableMapOf<String, String>()
                    for (columnName in columnNames) {
                        val value = cursor.getString(cursor.getColumnIndex(columnName)) ?: ""
                        rowData[columnName] = value
                    }
                    rows.add(rowData)
                }
            }
        } ?: Log.e(TAG, "Database is null!")

        return rows
    }




}