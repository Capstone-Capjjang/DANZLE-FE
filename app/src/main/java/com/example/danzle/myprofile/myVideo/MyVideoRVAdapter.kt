package com.example.danzle.myprofile.myVideo

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.danzle.VideoPlayerActivity
import com.example.danzle.data.remote.response.auth.MyVideoResponse
import com.example.danzle.databinding.MypageVideoRecyclerviewBinding


//	- RecyclerView.Adapter를 상속한 어댑터 클래스.
//	- info는 MyVideoResponse 객체 리스트로, 각 아이템의 데이터를 담고 있어.
//	- MyVideoResponse는 동영상에 대한 정보를 담은 데이터 클래스 (예: 아티스트, 곡 제목, 이미지 경로 등).

class MyVideoRVAdapter(private var info: ArrayList<MyVideoResponse>) :
    RecyclerView.Adapter<MyVideoRVAdapter.VideoViewHolder>() {
    // VideoViewHolder는 각 아이템 뷰의 정보를 관리
    // 	ViewBinding을 통해 레이아웃 XML의 뷰들과 연결
    inner class VideoViewHolder(private val binding: MypageVideoRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(response: MyVideoResponse) {
            binding.textView.text = "${response.artist} - ${response.songTitle}"

            // Glide를 사용해 네트워크 또는 로컬에서 이미지 로딩
            Glide.with(binding.image.context)
                .load(response.songImgPath)
                .into(binding.image)

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, VideoPlayerActivity::class.java)
                // videoPath 정보를 담아서 넘겨준다.
                intent.putExtra("videoPath", response.videoPath)
                context.startActivity(intent)
            }
        }
    }

    // 리스트 아이템 수 반환 → RecyclerView가 몇 개의 아이템을 보여줄지 알게됨
    override fun getItemCount(): Int {
        return info.size
    }

    // RecyclerView가 아이템 뷰를 처음 만들 때 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = MypageVideoRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoViewHolder(view)
    }

    // 현재 position에 해당하는 데이터를 ViewHolder에 바인딩 (화면에 출력)
    // position에 해당하는 데이터를 ViewHolder에 바인딩
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(info[position])
    }
}