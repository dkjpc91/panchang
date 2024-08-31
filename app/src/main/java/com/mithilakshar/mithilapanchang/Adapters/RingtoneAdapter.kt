package com.mithilakshar.mithilapanchang.Adapters

import java.text.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Room.Ringtone
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RingtoneAdapter(
    var ringtones: MutableList<Ringtone>,
    private val deleteRingtone: (Ringtone) -> Unit // Lambda to delete a Ringtone
) : RecyclerView.Adapter<RingtoneAdapter.RingtoneViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingtoneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm_data, parent, false)
        return RingtoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: RingtoneViewHolder, position: Int) {
        holder.bind(ringtones[position])
    }

    override fun getItemCount(): Int = ringtones.size

    inner class RingtoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.task_title)
        private val task_description: TextView = itemView.findViewById(R.id.task_description)
        private val dateTextView: TextView = itemView.findViewById(R.id.task_date)
        private val timeTextView: TextView = itemView.findViewById(R.id.task_time)
        private val delete: ImageView = itemView.findViewById(R.id.delete)

        fun bind(ringtone: Ringtone) {
            titleTextView.text = ringtone.title
            task_description.text = ringtone.message
            // Format the date time for display
            val formattedDate = convertMillisToDate(ringtone.dateTimeInMillis)
            val formattedTime = convertMillisToTime(ringtone.dateTimeInMillis)
            dateTextView.text ="दिनांक: " + formattedDate
            timeTextView.text = "समय : "+ formattedTime
            delete.setOnClickListener {
                val removedPosition = adapterPosition
                ringtones.removeAt(removedPosition)
                notifyItemRemoved(removedPosition)
                deleteRingtone(ringtone)  // Pass Ringtone object for deletion
            }
        }
    }

    fun setringtone(newlist: List<Ringtone>) {
        ringtones = newlist.toMutableList()
        notifyDataSetChanged()
    }

    fun convertMillisToDate(millis: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date(millis)
        return dateFormat.format(date)
    }

    fun convertMillisToDateTime(millis: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = Date(millis)
        return dateFormat.format(date)
    }

    fun convertMillisToTime(millis: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
        val date = Date(millis)
        return dateFormat.format(date)
    }
}
