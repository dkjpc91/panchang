package com.mithilakshar.mithilapanchang.UI.View

import android.app.DatePickerDialog
import android.app.TimePickerDialog

import android.os.Bundle


import android.widget.EditText
import android.widget.ListView

import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.Adapters.RingtoneAdapter
import com.mithilakshar.mithilapanchang.Adapters.RingtonePickerAdapter
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Room.Ringtone

import com.mithilakshar.mithilapanchang.Utility.AlarmHelper
import com.mithilakshar.mithilapanchang.Utility.MyApplication
import com.mithilakshar.mithilapanchang.ViewModel.RingtoneViewmodel
import com.mithilakshar.mithilapanchang.databinding.ActivityAlarmBinding

import androidx.lifecycle.Observer






import java.util.*

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

import android.os.Build


import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RequiresApi


import androidx.core.app.ActivityCompat


class AlarmActivity : AppCompatActivity() {

    private lateinit var alarmHelper: AlarmHelper

    private lateinit var selectRingtoneButton: AppCompatButton
    private lateinit var ringtoneRecyclerView: RecyclerView
    private lateinit var ringtoneAdapter: RingtoneAdapter
    var notificationurl: List<String> = arrayListOf()
    private val ringtoneViewModel: RingtoneViewmodel by viewModels {
        RingtoneViewmodel.RingtoneViewmodelFactory((application as MyApplication).repository)
    }
    private var selectedDateTime: Calendar = Calendar.getInstance()

    private val ringtones = arrayOf(
        R.raw.ram,
        R.raw.shyama,
        R.raw.jai_jai_bhairab,
        R.raw.maithili,
        R.raw.achyutam_keshavam,
        R.raw.adharam_madhuram,
        R.raw.bajrang_baan,
        R.raw.bajrang_baan_,
        R.raw.ganpati,
        R.raw.gayatri_mantra,
        R.raw.hanuman_chalisa,
        R.raw.hanuman_gyan_gun,
        R.raw.jai_hanuman,
        R.raw.jai_raja_raam,
        R.raw.jai_hanuman_sant,
        R.raw.kesari_ke_laal,
        R.raw.prabhu_ji,
        R.raw.ram_aayege,
        R.raw.serawaliye,
        R.raw.shashank_shekhar,
        R.raw.ye_chamak
    )

    private val REQUEST_EXACT_ALARM_PERMISSION_CODE = 100

    lateinit var binding: ActivityAlarmBinding
    private var alertDialog: AlertDialog? = null // Reference to AlertDialog

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Check if the permission is already granted



        selectRingtoneButton = binding.saveButton
        ringtoneRecyclerView = binding.ringtoneRecyclerView
        ringtoneRecyclerView.layoutManager = LinearLayoutManager(this)

        alarmHelper = AlarmHelper(this)






        ringtoneAdapter=RingtoneAdapter(mutableListOf()){

            ringtoneViewModel.deleteringtone(it)

            if (notificationurl.isNotEmpty()) {
                alarmHelper.cancelAlarm(it.title, it.message, it.selectedRingtone, notificationurl[0])

            } else {
                // Handle the case where notificationurl is empty

            }




        }

        ringtoneRecyclerView.apply {
            adapter = ringtoneAdapter
            layoutManager = LinearLayoutManager(this@AlarmActivity)
        }

        // Observe the LiveData
        ringtoneViewModel.allringtone.observe(this, Observer {
        it.let {
            ringtoneAdapter.setringtone(it)

        }
        })







        selectRingtoneButton.setOnClickListener {

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !alarmManager.canScheduleExactAlarms()) {
                // Permission might not be granted, handle accordingly
                requestExactAlarmPermission()
            } else {
                showDatePicker()
            }

        }









    }

    private fun showDatePicker() {
        val currentDate = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDateTime.set(Calendar.YEAR, year)
                selectedDateTime.set(Calendar.MONTH, month)
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showTimePicker()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)

        ).show()
    }
//app
    private fun showTimePicker() {
        val currentTime = Calendar.getInstance()
        TimePickerDialog(this, { _, hourOfDay, minute ->
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedDateTime.set(Calendar.MINUTE, minute)
            showRingtonePicker()
        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true).show()
    }

    private fun showRingtonePicker() {
        val ringtoneName = ringtones.map { resources.getResourceEntryName(it) }.toTypedArray()
        val ringtoneNames = arrayOf(
            "राम",
            "श्यामा",
            "जय जय भैरव",
            "बिनती",
            "अच्युतम केशवम",
            "अधरम मधुरम",
            "बजरंग बाण",
            "बजरंग-बाण",
            "गणपति",
            "गायत्री मंत्र",
            "हनुमान चालीसा",
            "हनुमान ज्ञान गुण",
            "जय हनुमान",
            "जय राजा राम",
            "जय हनुमान संत",
            "केसरी के लाल",
            "प्रभु जी",
            "राम आएंगे",
            "सेरावालिये",
            "शशांक शेखर",
            "ये चमक"
        )

        // Dialog to collect title and message text
        val dialogView = layoutInflater.inflate(R.layout.dialog_ringtone_details, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.titleEditText)
        val messageEditText = dialogView.findViewById<EditText>(R.id.messageEditText)

        alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("आगे") { _, _ ->

                val title = if (titleEditText.text.isBlank()) "अलार्म" else titleEditText.text.toString()
                val messageText = if (messageEditText.text.isBlank()) "पंचांग" else messageEditText.text.toString()



                // Save the selected time, title, and message
                val ringtonePickerView = ListView(this)


                val ringtonePickerDialog = AlertDialog.Builder(this)
                    .setTitle("मनपसंद टोन")
                    .setView(ringtonePickerView)
                    .create()

                ringtonePickerDialog.show()

                ringtonePickerView.adapter = RingtonePickerAdapter(this, ringtoneNames, ringtones) { selectedRingtone, selectedTitle, selectedMessage ->


                    Toast.makeText(this, "picker tone# $selectedRingtone", Toast.LENGTH_LONG)
                        .show()
                    saveRingtone(selectedRingtone, title, messageText)
                    ringtoneAdapter.notifyDataSetChanged()
                    alertDialog?.dismiss()
                    ringtonePickerDialog.dismiss()// Dismiss the main AlertDialog after selecting the ringtone
                }


            }
            .show()


    }


    private fun saveRingtone(selectedRingtone: Int, title: String, messageText: String) {

        ringtoneViewModel.insertringtone(Ringtone(0,messageText,title,selectedRingtone,selectedDateTime.timeInMillis))

        // Add to the list to display
        ringtoneAdapter.notifyDataSetChanged()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateTime.timeInMillis
        }
        Toast.makeText(this, "अलार्म ${title} सेट $selectedRingtone", Toast.LENGTH_LONG)
            .show()
        if (notificationurl.isNotEmpty()) {
            alarmHelper.setAlarm(calendar, title, messageText, selectedRingtone, notificationurl[0])
        } else {
            // Handle empty notificationurl case: set a default URL or show an error
            alarmHelper.setAlarm(calendar, title, messageText, selectedRingtone,"https://i.pinimg.com/564x/b7/de/a6/b7dea6a1800c6f9407083da4a93ba055.jpg")
            // Optionally, set a default value or do not proceed
        }


    }



    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API level 33 or above
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                REQUEST_EXACT_ALARM_PERMISSION_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_EXACT_ALARM_PERMISSION_CODE) {
            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (granted) {
                // Permission granted, proceed with alarms
                showDatePicker()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.SCHEDULE_EXACT_ALARM)) {
                    // User has opted out of being asked again

                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.data = Uri.parse("package:" + packageName)
                    startActivity(intent)  // Missing semicolon
                    Toast.makeText(this, "Permission required for exact alarms. Please grant it from App Settings.", Toast.LENGTH_LONG).show()

                    } else {
                    // User denied but can still be prompted again (might not trigger prompt)
                    // Explain why the permission is needed (optional)
                }
            }
        }
    }

}
