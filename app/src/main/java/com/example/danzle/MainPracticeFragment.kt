package com.example.danzle

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.danzle.challenge.ChallengeMusicSelect
import com.example.danzle.challenge.FragmentChallenge
import com.example.danzle.correction.CorrectionMusicSelect
import com.example.danzle.correction.FragmentCorrection
import com.example.danzle.databinding.ChallengeFragmentBinding
import com.example.danzle.databinding.CorrectionFragmentBinding
import com.example.danzle.databinding.FragmentMainPracticeBinding
import com.example.danzle.practice.FragmentPractice

class MainPracticeFragment: Fragment() {

    private var _binding : FragmentMainPracticeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPracticeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.viewPager
        // the loading page
        viewPager.offscreenPageLimit = 3


        // page margin & animation
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pagerWidth)
        val screenWidth = resources.displayMetrics.widthPixels
        val offsetPx = screenWidth - pageMarginPx - pagerWidth

        // make us look side pages
        viewPager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx
        }
        viewPager.adapter = FragmentAdapter(requireActivity(),3)
        // requireActivity()는 이 Fragment를 호스팅하고 있는 액티비티를 가져온다.

        // changing the background color according to viewpager card
        // registerOnPageChangeCallback 메서드를 사용하여 페이지 변경이벤트를 확인한다.
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                val color = when(position) {
                    0 -> R.drawable.practice_background_color
                    1 -> R.drawable.correction_background_color
                    else -> R.drawable.challenge_background_color

                }
                binding.root.setBackgroundResource(color)
            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


class FragmentAdapter(
    fragmentActivity: FragmentActivity,
    private val tabCount: Int
): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return tabCount
    }

    override fun createFragment(position: Int): Fragment {
//        if (position == 1){
//            FragmentPractice.setBackgroundResource
//        }
        return when (position) {
            0 -> FragmentPractice()
            1 -> FragmentCorrection()
            else -> FragmentChallenge()
        }
    }
}
