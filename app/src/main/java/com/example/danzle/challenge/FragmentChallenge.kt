package com.example.danzle.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.danzle.R

class FragmentChallenge: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // to show fragment view
        return inflater.inflate(R.layout.challenge_fragment, container, false)
    }
}