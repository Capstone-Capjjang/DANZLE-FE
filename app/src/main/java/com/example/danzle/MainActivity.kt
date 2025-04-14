package com.example.danzle

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.danzle.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // In MainActivity, it's defined start fragment as HomeFragment
        // 일단 임시로 practice를 시작으로 설정해줌
        replaceFragment(MainPracticeFragment())
        binding.bottomNavigationView.selectedItemId = R.id.dance


        // navigation
        binding.bottomNavigationView.setOnItemSelectedListener{ item ->
            when(item.itemId){
                R.id.dance -> {
                    replaceFragment(MainPracticeFragment())
                    true
                }
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.my -> {
                    replaceFragment(MyProfileFragment())
                    true
                }
                else -> false
            }
        }

    }

    // changing the view method
    fun replaceFragment(fragment: Fragment){
        // frame_layout이 들어오는 영역에 입력해준 fragment가 할당된다.
        // FragmentManager는 앱 프래그먼트에서 프래그먼트를 추가, 삭제, 교체하고 백스택에 추가하는 작업을 실행하는 클래스
        // Activity에서 접근하기 위해서 supportFragmentManager를 써야한다.

        // FragmentTransaction
        // FragmentManager를 사용하여 Fragment를 추가, 제거, 교체 등의 작업을 수행하는 일련의 과정을 의미한다.
        // 데이터베이스에서와 같이 일련의 작업들을 묶어 처리하는 개념으로 Fragment 관련 작업을 한 번에 처리하고 성공적으로 완료되면 commit 할 수 있다.
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
    }

}