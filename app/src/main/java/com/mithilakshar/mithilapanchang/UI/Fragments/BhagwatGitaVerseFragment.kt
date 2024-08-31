package com.mithilakshar.mithilapanchang.UI.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.mithilakshar.mithilapanchang.Adapters.GitaVerseAdapter

import com.mithilakshar.mithilapanchang.Utility.dbHelper
import com.mithilakshar.mithilapanchang.databinding.FragmentBhagwatGitaVerseBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BhagwatGitaVerseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BhagwatGitaVerseFragment : Fragment() {

    lateinit var binding: FragmentBhagwatGitaVerseBinding

    private val args: BhagwatGitaVerseFragmentArgs by navArgs()
    private lateinit var dbHelper: dbHelper
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentBhagwatGitaVerseBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chapterName = args.chapternameq

        binding.chaptrname.text=chapterName.toString()

        dbHelper = dbHelper(requireContext(), "Gita.db")
        val rows = dbHelper.getRowsByChapterName(chapterName)
        val adapter = GitaVerseAdapter(rows)
        binding.gitaverserecyclerView.adapter = adapter
    }


}