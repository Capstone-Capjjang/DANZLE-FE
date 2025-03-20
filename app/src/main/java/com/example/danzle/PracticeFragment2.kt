package com.example.danzle

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.danzle.challenge.FragmentChallenge
import com.example.danzle.correction.FragmentCorrect
import com.example.danzle.practice.FragmentPractice

class PracticeMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_practice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.offscreenPageLimit = 3
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pagerWidth)
        val screenWidth = resources.displayMetrics.widthPixels
        val offsetPx = screenWidth - pageMarginPx - pagerWidth

        viewPager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx
        }
        viewPager.adapter = FragmentAdapter(this,3)


        // changing activity
//        findViewById<ViewPager2>(R.id.viewPager).setOnClickListener{
//            when(supportFragmentManager){
//                FragmentPractice ->
//            }
//        }

    }
}

class FragmentAdapter(
    fragmentActivity: FragmentActivity,
    val tabCount: Int
): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return tabCount
    }

    override fun createFragment(position: Int): Fragment {
//        if (position == 1){
//            FragmentPractice.setBackgroundResource
//        }
        when (position) {
            0 -> return FragmentPractice()
            1 -> return FragmentChallenge()
            else -> return FragmentCorrect()
        }
    }
}