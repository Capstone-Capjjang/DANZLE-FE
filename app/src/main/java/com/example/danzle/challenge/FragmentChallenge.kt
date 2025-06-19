package com.example.danzle.challenge

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.danzle.databinding.ChallengeFragmentBinding

class FragmentChallenge: Fragment() {
    private var _binding : ChallengeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // to show fragment view
        _binding = ChallengeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // root -> 특정 카드 전체를 감싸는 상위 뷰에 대해, 그걸 클릭 타겟
        binding.root.setOnClickListener{
            val intent = Intent(requireContext(), ChallengeMusicSelect::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}