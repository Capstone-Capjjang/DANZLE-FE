package com.example.danzle.practice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.danzle.databinding.PracticeFragmentBinding

// Fragment 상속
// fragment는 액티비티 내에 삽입되어 UI 일부를 구성하거나 화면 일부를 독립적으로 구성하는 데 사용
class FragmentPractice: Fragment() {
    private var _binding : PracticeFragmentBinding? = null
    private val binding get() = _binding!!

    // 처음 생성할 때 호출
    // XML 레이아웃 대신에 ViewBinding을 통해 UI를 inflate하고
    // 루트 뷰를 리턴해서 Fragment의 레이아웃으로 사용
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // to show fragment view
        _binding = PracticeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    //  RecyclerView 나 ViewPager2에 사용되는 Adapter는 여기서 해주는 것이 좋다.
    // UI 이벤트를 여기서 설정하는 것이 일반적
    // binding.root는 이 프래그먼트 전체를 감싸는 상위 뷰를 의미
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // page changing when clicking view page's certain slide
        // root -> 특정 카드 전체를 감싸는 상위 뷰에 대해, 그걸 클릭 타겟
        binding.root.setOnClickListener{
            val intent = Intent(requireContext(), PracticeMusicSelect::class.java)
            startActivity(intent)
        }
    }

    // 메모리 누수 방지를 위해 사용
    // fragment의 뷰가 파괴될 때 _binding을 null로 해제
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}