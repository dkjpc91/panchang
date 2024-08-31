import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.UI.Fragments.BhagwatGitaChapterFragmentDirections
import com.mithilakshar.mithilapanchang.Utility.dbHelper
import com.mithilakshar.mithilapanchang.databinding.ItemGitachapterBinding

// Import your View Binding class for the item layout

class GitaChapterAdapter(private val chapterNames: List<dbHelper.Chapter>) : RecyclerView.Adapter<GitaChapterAdapter.ChapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val binding = ItemGitachapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapterName = chapterNames[position]
        holder.bind(chapterName)
    }

    override fun getItemCount(): Int {
        return chapterNames.size
    }

    inner class ChapterViewHolder(private val binding: ItemGitachapterBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Set click listener on the item
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Get chapter name associated with the clicked item
                    val chapterName = chapterNames[position]

                    // Navigate to destination fragment using Navigation Component with argument



                   val action = BhagwatGitaChapterFragmentDirections.actionBhagwatGitaChapterFragmentToBhagwatGitaVerseFragment(chapterName.chapterName)
                   binding.root.findNavController().navigate(action)
                }
            }
        }

        fun bind(chapterName: dbHelper.Chapter) {
            binding.apply {
                chaptername.text=translateToHindidate(chapterName.chapternumber)+". "+ chapterName.chapterName
                shlokaCount.text=chapterName.uid.toString()+" श्लोक "
                chapterdesc.text=chapterName.description

            }
        }
    }

    private fun translateToHindidate(date: String): String? {
        // Manually create a mapping for English to Hindi month names
        val nmap: MutableMap<String, String> = HashMap()
        nmap["1"] = "१"
        nmap["2"] = "२"
        nmap["3"] = "३"
        nmap["4"] = "४"
        nmap["5"] = "५"
        nmap["6"] = "६"
        nmap["7"] = "७"
        nmap["8"] = "८"
        nmap["9"] = "९"
        nmap["10"] = "१०"
        nmap["11"] = "११"
        nmap["12"] = "१२"
        nmap["13"] = "१३"
        nmap["14"] = "१४"
        nmap["15"] = "१५"
        nmap["16"] = "१६"
        nmap["17"] = "१७"
        nmap["18"] = "१८"
        nmap["19"] = "१९"
        nmap["20"] = "२०"
        nmap["21"] = "२१"
        nmap["22"] = "२२"
        nmap["23"] = "२३"
        nmap["24"] = "२४"
        nmap["25"] = "२५"
        nmap["26"] = "२६"
        nmap["27"] = "२७"
        nmap["28"] = "२८"
        nmap["29"] = "२९"
        nmap["30"] = "३०"
        nmap["2024"] = "२०२४"
        nmap["2025"] = "२०२५"
        nmap["2026"] = "२०२६"
        nmap["2027"] = "२०२७"
        nmap["2028"] = "२०२८"
        nmap["2029"] = "२०२९"
        nmap["2030"] = "२०३०"
        // Return the translated month name
        return nmap[date]
    }
}
