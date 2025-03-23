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

    fun getLastRowIndex(tableName: String): Int {
        var lastRowIndex = -1
        db?.let {
            val cursor = it.rawQuery("SELECT * FROM $tableName", null)
            cursor.use { c ->
                if (c.moveToLast()) {
                    lastRowIndex = c.position // This gives the index of the last row
                }
            }
        }
        return lastRowIndex
    }
    @SuppressLint("Range")
    fun getFirstHolidayMatchRow(monthName: String, startDate: String, tableName: String): Map<String, String>? {
        val rowData = mutableMapOf<String, String>()

        // Validate input parameters
        val startDay = startDate.toIntOrNull()
        if (monthName.isEmpty() || startDate.isEmpty() || startDay == null) {
            Log.w(TAG, "Invalid parameters: monthName=$monthName, startDate=$startDate")
            return null // Return null if parameters are invalid
        }

        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading holidays.")
                return null // Return null if database is not open
            }

            // SQL query to select rows with the specified month and datenumber >= startDate
            val query = "SELECT * FROM $tableName WHERE month = ? AND CAST(datenumber AS INTEGER) >= ?"
            val selectionArgs = arrayOf(monthName, startDay.toString())

            try {
                // Execute the query and process the cursor
                database.rawQuery(query, selectionArgs)?.use { cursor ->
                    // Loop through the cursor to find the first match
                    while (cursor.moveToNext()) {
                        val monthFromDb = cursor.getString(cursor.getColumnIndex("month"))
                        val datenumberFromDb = cursor.getInt(cursor.getColumnIndex("datenumber"))
                        val nameFromDb = cursor.getString(cursor.getColumnIndex("name"))
                        val descFromDb = cursor.getString(cursor.getColumnIndex("desc"))

                        // Log for debugging
                        Log.d(TAG, "Checking row: month=$monthFromDb, datenumber=$datenumberFromDb")

                        // If the current row matches the month and datenumber condition
                        if (monthFromDb == monthName && datenumberFromDb >= startDay) {
                            // Log the first matching row data
                            Log.d(TAG, "Found matching row: $monthFromDb, $datenumberFromDb, $nameFromDb, $descFromDb")

                            // Populate the rowData map with the values of the matching row
                            rowData["month"] = monthFromDb
                            rowData["datenumber"] = datenumberFromDb.toString()
                            rowData["name"] = nameFromDb
                            rowData["desc"] = descFromDb

                            return rowData // Return the row data immediately after the first match
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error querying holidays: ${e.message}", e)
            }
        }

        // If no matching row is found, return null
        return null
    }

    fun getFirstHolidayMatchOriginalIndex(monthName: String, startDate: String, tableName: String): Int {
        // Validate input parameters
        val startDay = startDate.toIntOrNull()
        if (monthName.isEmpty() || startDate.isEmpty() || startDay == null) {
            Log.w(TAG, "Invalid parameters: monthName=$monthName, startDate=$startDate")
            return -1 // Return -1 if parameters are invalid
        }

        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading holidays.")
                return -1 // Return -1 if database is not open
            }

            // SQL query to select all rows in the table (no filtering)
            val query = "SELECT * FROM $tableName"
            try {
                // Execute the query and process the cursor
                database.rawQuery(query, null)?.use { cursor ->
                    var rowIndex = 0  // Keep track of the original row index
                    while (cursor.moveToNext()) {
                        val monthFromDb = cursor.getString(cursor.getColumnIndex("month"))
                        val datenumberFromDb = cursor.getInt(cursor.getColumnIndex("datenumber"))
                        val nameFromDb = cursor.getString(cursor.getColumnIndex("name"))
                        val descFromDb = cursor.getString(cursor.getColumnIndex("desc"))

                        // Log for debugging
                        Log.d(TAG, "Row $rowIndex: month=$monthFromDb, datenumber=$datenumberFromDb")

                        // Check if the row matches the month and datenumber criteria
                        if (monthFromDb == monthName && datenumberFromDb >= startDay) {
                            // Log and return the original index of the matching row
                            Log.d(TAG, "Found matching row at original index: $rowIndex")
                            // Return the index from the original (unfiltered) list
                            return rowIndex
                        }

                        rowIndex++ // Increment the row index for the next row
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error querying holidays: ${e.message}", e)
            }
        }

        // If no matching row is found, return -1
        return -1
    }

    fun getRandomHolidayIndex(monthName: String, startDate: String, tableName: String): Int {
        // Validate input parameters
        val startDay = startDate.toIntOrNull()
        if (monthName.isEmpty() || startDate.isEmpty() || startDay == null) {
            Log.w(TAG, "Invalid parameters: monthName=$monthName, startDate=$startDate")
            return -1 // Return -1 if parameters are invalid
        }

        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading holidays.")
                return -1 // Return -1 if database is not open
            }

            // First, find the index of the first matching holiday
            val firstMatchIndex = getFirstHolidayMatchOriginalIndex(monthName, startDate, tableName)
            if (firstMatchIndex == -1) {
                Log.w(TAG, "No matching holiday found.")
                return -1 // Return -1 if no matching row is found
            }

            // Next, find the last row index in the table
            val lastRowIndex = getLastRowIndex(tableName)
            if (lastRowIndex == -1) {
                Log.w(TAG, "Error retrieving the last row index.")
                return -1 // Return -1 if the last row index can't be found
            }

            // Ensure that firstMatchIndex <= lastRowIndex
            if (firstMatchIndex > lastRowIndex) {
                Log.w(TAG, "First match index is greater than last row index.")
                return -1
            }

            // Generate a random index between firstMatchIndex and lastRowIndex
            val randomIndex = (firstMatchIndex..lastRowIndex).random()
            Log.d(TAG, "Random index between $firstMatchIndex and $lastRowIndex: $randomIndex")

            // Return the random index
            return randomIndex
        }

        // If database is not open or some error occurs, return -1
        return -1
    }
    fun getRandomHoliday(monthName: String, startDate: String, tableName: String): Map<String, String>? {
        // Validate input parameters
        val startDay = startDate.toIntOrNull()
        if (monthName.isEmpty() || startDate.isEmpty() || startDay == null) {
            Log.w(TAG, "Invalid parameters: monthName=$monthName, startDate=$startDate")
            return null // Return null if parameters are invalid
        }

        db?.let { database ->
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading holidays.")
                return null // Return null if database is not open
            }

            // First, find the index of the first matching holiday
            val firstMatchIndex = getFirstHolidayMatchOriginalIndex(monthName, startDate, tableName)
            if (firstMatchIndex == -1) {
                Log.w(TAG, "No matching holiday found.")
                return null // Return null if no matching row is found
            }

            // Next, find the last row index in the table
            val lastRowIndex = getLastRowIndex(tableName)
            if (lastRowIndex == -1) {
                Log.w(TAG, "Error retrieving the last row index.")
                return null // Return null if the last row index can't be found
            }

            // Ensure that firstMatchIndex <= lastRowIndex
            if (firstMatchIndex > lastRowIndex) {
                Log.w(TAG, "First match index is greater than last row index.")
                return null
            }

            // Generate a random index between firstMatchIndex and lastRowIndex
            val randomIndex = (firstMatchIndex..lastRowIndex).random()
            Log.d(TAG, "Random index between $firstMatchIndex and $lastRowIndex: $randomIndex")

            // SQL query to select all rows in the table (no filtering)
            val query = "SELECT * FROM $tableName"
            try {
                database.rawQuery(query, null)?.use { cursor ->
                    var rowIndex = 0
                    while (cursor.moveToNext()) {
                        if (rowIndex == randomIndex) {
                            // Extract values from the cursor at the random index
                            val holidayData = mutableMapOf<String, String>()
                            holidayData["month"] = cursor.getString(cursor.getColumnIndex("month"))
                            holidayData["date"] = cursor.getString(cursor.getColumnIndex("date"))
                            holidayData["name"] = cursor.getString(cursor.getColumnIndex("name"))
                            holidayData["desc"] = cursor.getString(cursor.getColumnIndex("desc"))
                            Log.d(TAG, "Random holiday data: $holidayData")
                            return holidayData // Return the row data as a map
                        }
                        rowIndex++ // Increment row index
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error querying holidays: ${e.message}", e)
            }
        }

        // If database is not open or some error occurs, return null
        return null
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
    @SuppressLint("Range")
    fun tithilist(monthName: String, dbName: String, tableName: String): List<Map<String, String>> {
        val tithiList = mutableListOf<Map<String, String>>()
        db?.let { database ->
            // Check if the database is open
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for reading Tithi data for month: $monthName")
                return emptyList()
            }

            // Query to select rows where the Timing column contains the specified month
            val query = "SELECT * FROM $tableName WHERE Timing LIKE ?"
            val selectionArgs = arrayOf("%$monthName%") // Search for the month name in the Timing column

            // Execute the query
            database.rawQuery(query, selectionArgs)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val rowData = mutableMapOf<String, String>()
                    // Extract data from the cursor
                    val tithi = cursor.getString(cursor.getColumnIndex("Tithi"))
                    val timing = cursor.getString(cursor.getColumnIndex("Timing"))
                    val hindiTithi = cursor.getString(cursor.getColumnIndex("Hindi Tithi"))
                    val hindiTiming = cursor.getString(cursor.getColumnIndex("Hindi Timinig"))

                    // Add data to the map
                    rowData["Tithi"] = tithi
                    rowData["Timing"] = timing
                    rowData["Hindi Tithi"] = hindiTithi
                    rowData["Hindi Timinig"] = hindiTiming

                    // Add the row to the list
                    tithiList.add(rowData)
                }
            }
        }

        // If no Tithi data is found, return a default value
        if (tithiList.isEmpty()) {
            val defaultTithi = mutableMapOf<String, String>()
            defaultTithi["Tithi"] = "अपडेट प्रक्रिया में"
            defaultTithi["Timing"] = ""
            defaultTithi["Hindi Tithi"] = "अपडेट प्रक्रिया में"
            defaultTithi["Hindi Timinig"] = ""
            tithiList.add(defaultTithi)
        }

        return tithiList
    }

    @SuppressLint("Range")
    fun tithiSearchFilter(searchText: String, tableName: String): List<Map<String, String>> {
        val filteredResults = mutableListOf<Map<String, String>>()
        val TAG = "TithiSearchFilter"

        db?.let { database ->
            // Check if the database is open
            if (!database.isOpen) {
                Log.w(TAG, "Database not open for searching with text: $searchText")
                return emptyList()
            }

            Log.d(TAG, "Searching in table: $tableName for text: $searchText")

            // Query to filter rows by the Tithi column
            val query = "SELECT * FROM $tableName WHERE Tithi LIKE ?"
            val selectionArgs = arrayOf("%$searchText%")

            Log.d(TAG, "Executing query: $query with args: ${selectionArgs.joinToString()}")

            // Execute the query
            database.rawQuery(query, selectionArgs)?.use { cursor ->
                val rowCount = cursor.count
                Log.d(TAG, "Number of rows found: $rowCount")

                // Iterate through the cursor and extract data
                while (cursor.moveToNext()) {
                    val rowData = mutableMapOf<String, String>()

                    // Extract data from the cursor
                    val tithi = cursor.getString(cursor.getColumnIndex("Tithi"))
                    val timing = cursor.getString(cursor.getColumnIndex("Timing"))
                    val hindiTithi = cursor.getString(cursor.getColumnIndex("Hindi Tithi"))
                    val hindiTiming = cursor.getString(cursor.getColumnIndex("Hindi Timinig"))

                    Log.d(TAG, "Retrieved row - Tithi: $tithi, Timing: $timing, Hindi Tithi: $hindiTithi, Hindi Timing: $hindiTiming")

                    // Add data to the map
                    rowData["Tithi"] = tithi
                    rowData["Timing"] = timing
                    rowData["Hindi Tithi"] = hindiTithi
                    rowData["Hindi Timinig"] = hindiTiming

                    // Add the row to the filtered results
                    filteredResults.add(rowData)
                }
            }
        } ?: Log.e(TAG, "Database reference is null")

        Log.d(TAG, "Filtered results: $filteredResults")
        return filteredResults
    }

}