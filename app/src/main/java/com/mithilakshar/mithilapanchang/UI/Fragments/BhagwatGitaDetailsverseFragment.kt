package com.mithilakshar.mithilapanchang.UI.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Utility.InAppReviewUtil
import com.mithilakshar.mithilapanchang.Utility.ViewShareUtil
import com.mithilakshar.mithilapanchang.Utility.dbHelper
import com.mithilakshar.mithilapanchang.databinding.FragmentBhagwatGitaDetailsverseBinding
import com.mithilakshar.mithilapanchang.databinding.FragmentBhagwatGitaVerseBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BhagwatGitaDetailsverseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BhagwatGitaDetailsverseFragment : Fragment() {
    lateinit var binding: FragmentBhagwatGitaDetailsverseBinding
    private lateinit var adView6: AdView
    private val args: BhagwatGitaDetailsverseFragmentArgs by navArgs()
    lateinit var dbHelper: dbHelper
    private lateinit var inAppReviewUtil: InAppReviewUtil
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

        binding= FragmentBhagwatGitaDetailsverseBinding.inflate(layoutInflater,container,false)
        adView6 = binding.adView6

        val adRequest = AdRequest.Builder().build()
        // Set an AdListener to make the AdView visible when the ad is loaded
        adView6.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Make the AdView visible when the ad is loaded
                adView6.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                // Optionally, you can log or handle the error here
            }
        }
        adView6.loadAd(adRequest)

        dbHelper = dbHelper(requireContext(), "Gita.db")
        val rowsvalue= dbHelper.getRowById(args.versenumber)

        inAppReviewUtil = InAppReviewUtil(requireActivity())

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            inAppReviewUtil.startReviewFlow()
        }, 30000)


        rowsvalue?.let {
            binding.apply {

                // Access rowData values and set them in TextViews
                sholktext.text = "${it["shlok"] as? String ?: "N/A"}"
                descsholk.text = "${it["maithili"] as? String ?: "N/A"}"
                shlokdesc.text = "${it["vyakhya"] as? String ?:"N/A"}"
            }

            binding.sharegita.setOnClickListener {

                ViewShareUtil.shareViewAsImageDirectly(binding.sharegitascreen,requireContext())
            }

        }
        return binding.root
    }

}