package com.example.danzle.myprofile.myScore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.danzle.R
import com.example.danzle.data.remote.response.auth.MyScoreResponse
import com.example.danzle.databinding.ScoreRecyclerviewBinding

class MyScoreRVAdapter(
    private val info: ArrayList<MyScoreResponse>,
    private val listener: RecyclerViewEvent
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class RecyclerViewHolder(private val binding: ScoreRecyclerviewBinding) : // viewholder는 각각의 아이템 뷰를 관리, ScoreRecyclerviewBinding은 XML 레이아웃을 바인딩한 객체
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(score: MyScoreResponse) { // 하나의 MyScoreResponse 데이터를 레이아웃에 바인딩해주는 역할
            Glide.with(binding.root.context)
                .load(score.song.coverImagePath)
                .into(binding.songImage)
            binding.songName.text = score.song.title
            binding.singer.text = score.song.artist
            binding.score.text = score.feedback

            val feedback = score.feedback

            when (feedback) {
                "Perfect" -> binding.scoreImage.setImageResource(R.drawable.star_perfect)
                "Good" -> binding.scoreImage.setImageResource(R.drawable.star_good)
                "Normal" -> binding.scoreImage.setImageResource(R.drawable.star_normal)
                "Bad" -> binding.scoreImage.setImageResource(R.drawable.star_bad)
                "Miss" -> binding.scoreImage.setImageResource(R.drawable.star_miss)
                else -> binding.scoreImage.setImageResource(R.drawable.star_miss)
            }
            binding.root.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }


    // RecyclerView가 새로운 뷰를 만들 때 호출됩니다.
    // ScoreRecyclerviewBinding.inflate(...)로 XML 뷰를 만들고, ViewHolder에 넘겨줍니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScoreRecyclerviewBinding.inflate(inflater, parent, false)
        return RecyclerViewHolder(binding)
    }

    // 스크롤하면서 뷰가 화면에 표시될 때마다 해당 위치의 데이터를 bind() 함수로 넘긴다.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecyclerViewHolder) {
            holder.bind(info[position])
        }
    }

    // 아이템 수 반환
    override fun getItemCount(): Int = info.size

    // item click listener
    interface RecyclerViewEvent {
        fun onItemClick(position: Int)
    }
}
