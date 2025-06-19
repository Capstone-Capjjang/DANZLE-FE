package com.example.danzle.myprofile.myVideo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.danzle.R
import com.example.danzle.data.api.DanzleSharedPreferences
import com.example.danzle.data.api.RetrofitApi
import com.example.danzle.data.remote.response.auth.MyVideoResponse
import com.example.danzle.databinding.ActivityMyVideoBinding
import com.example.danzle.enum.VideoMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyVideo : AppCompatActivity() {

    private lateinit var binding: ActivityMyVideoBinding

    private lateinit var practiceAdapter: MyVideoRVAdapter
    private lateinit var correctionAdapter: MyVideoRVAdapter
    private lateinit var challengeAdapter: MyVideoRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.practiceMore.setOnClickListener {
            startActivity(Intent(this@MyVideo, PracticeVideoRepository::class.java))
        }
        binding.correctionMore.setOnClickListener {
            startActivity(Intent(this@MyVideo, CorrectionVideoRepository::class.java))
        }
        binding.challengeMore.setOnClickListener {
            startActivity(Intent(this@MyVideo, ChallengeVideoRepository::class.java))
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        practiceAdapter = MyVideoRVAdapter(arrayListOf())
        correctionAdapter = MyVideoRVAdapter(arrayListOf())
        challengeAdapter = MyVideoRVAdapter(arrayListOf())

        binding.practiceVideoRecyclerview.adapter = practiceAdapter
        binding.correctionVideoRecyclerview.adapter = correctionAdapter
        binding.challengeVideoRecyclerview.adapter = challengeAdapter

        binding.practiceVideoRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.correctionVideoRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.challengeVideoRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val itemSpacing = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)

        binding.practiceVideoRecyclerview.addItemDecoration(
            HorizontalItemSpacingDecoration(
                itemSpacing
            )
        )
        binding.correctionVideoRecyclerview.addItemDecoration(
            HorizontalItemSpacingDecoration(
                itemSpacing
            )
        )
        binding.challengeVideoRecyclerview.addItemDecoration(
            HorizontalItemSpacingDecoration(
                itemSpacing
            )
        )

        retrofitMyVideo()
    }

    //about retrofit
    private fun retrofitMyVideo() {
        val token = DanzleSharedPreferences.getAccessToken()
        val authHeader = "Bearer $token"

        val userId = DanzleSharedPreferences.getUserId()

        if (token.isNullOrEmpty() || userId == null) {
            // Fragment는 requireContext, Activity는 this
            Toast.makeText(this@MyVideo, "You have to sign in.", Toast.LENGTH_SHORT).show()
            return
        }

        val retrofit = RetrofitApi.getMyVideoInstance()
        retrofit.getMyVideo(authHeader)
            .enqueue(object : Callback<List<MyVideoResponse>> {
                override fun onResponse(
                    call: Call<List<MyVideoResponse>>,
                    response: Response<List<MyVideoResponse>>
                ) {
                    if (response.isSuccessful) {
                        val myVideoList = response.body() ?: emptyList()
                        // separate data
                        // enum class로 선언되어 있어서 아래와 같이 VideoMode.PRACTICE로 불러와야 된다.
                        val practiceList = myVideoList.filter { it.mode == VideoMode.PRACTICE }
                        val correctionList = myVideoList.filter { it.mode == VideoMode.ACCURACY }
                        val challengeList = myVideoList.filter { it.mode == VideoMode.CHALLENGE }

                        // retrofit 응답 성공 시
                        practiceAdapter.updateList(practiceList)
                        correctionAdapter.updateList(correctionList)
                        challengeAdapter.updateList(challengeList)

                    } else {
                          Toast.makeText(
                            this@MyVideo,
                            "Fail to MyVideo: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<MyVideoResponse>>, t: Throwable) {
                    Toast.makeText(this@MyVideo, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

class HorizontalItemSpacingDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: android.graphics.Rect,
        view: android.view.View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        outRect.right = spacing
        if (position == 0) {
            outRect.left = spacing // 첫 번째 아이템 왼쪽도 간격
        }
    }
}
