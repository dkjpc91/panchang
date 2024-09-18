package com.mithilakshar.mithilapanchang.Room
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mithilakshar.mithilapanchang.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


@Database(entities = [Ringtone::class], version = 1, exportSchema = false)
abstract class RingtoneDatabase : RoomDatabase() {
    abstract fun ringtonedao(): RingtoneDao

    companion object {
        @Volatile
        private var INSTANCE: RingtoneDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RingtoneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RingtoneDatabase::class.java,
                    "ringtone_database"
                ).addCallback(RingtoneDatabaseCallback(context,scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class RingtoneDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(context, database.ringtonedao())
                }
            }
        }

        suspend fun populateDatabase(context: Context, ringtoneDao: RingtoneDao) {
            // Get the current time in milliseconds
            val calendar = Calendar.getInstance()

            // Set time to 6 AM
            calendar.set(Calendar.HOUR_OF_DAY, 6)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Add one day to the current date
            calendar.add(Calendar.DAY_OF_YEAR, 1)

            // Insert the dummy value
            val dummyRingtone = Ringtone(
                id = 1,
                message = "सुप्रभात"  ,
                title = "",
                selectedRingtone = R.raw.jai_jai_bhairab,
                dateTimeInMillis = calendar.timeInMillis
            )
            val calendar2 = Calendar.getInstance()

// Add 1 minute to the current time
            calendar2.add(Calendar.MINUTE, 1)

            val futureTimeInMillis = calendar2.timeInMillis

            val dummyRingtone1 = Ringtone(
                id = 2,
                message = "सुप्रभात"  ,
                title = "",
                selectedRingtone = R.raw.jai_jai_bhairab,
                dateTimeInMillis = futureTimeInMillis
            )
            ringtoneDao.insertringtone(dummyRingtone)

        }
    }
}