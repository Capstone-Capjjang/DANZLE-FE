package com.example.danzle.practice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.danzle.databinding.PracticeFragmentBinding

class FragmentPractice: Fragment() {
    private var _binding : PracticeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // to show fragment view
        _binding = PracticeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // page changing when clicking view page's certain slide
        // root -> 특정 카드 전체를 감싸는 상위 뷰에 대해, 그걸 클릭 타겟
        binding.root.setOnClickListener{
            val intent = Intent(requireContext(), PracticeMusicSelect::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}