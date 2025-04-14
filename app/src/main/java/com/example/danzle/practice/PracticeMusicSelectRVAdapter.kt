package com.example.danzle.practice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.danzle.data.remote.response.auth.PracticeMusicSelectResponse
import com.example.danzle.databinding.SelectsongBlueRecyclerviewBinding
import com.example.danzle.databinding.SelectsongPinkRecyclerviewBinding

class PracticeMusicSelectRVAdapter(
    private val info: ArrayList<PracticeMusicSelectResponse>,
    private val listener: RecyclerViewEvent
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // blue -> even
    inner class BlueViewHolder(private val binding: SelectsongBlueRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(response: PracticeMusicSelectResponse, position: Int) {
            binding.blueSongNumber.text = (position + 1).toString()
            binding.blueSongName.text = response.title
            binding.blueSinger.text = response.artist
            // Glide를 사용해 네트워크 또는 로컬에서 이미지 로딩
            Glide.with(binding.blueSongImage.context)
                .load(response.coverImagePath)
                .into(binding.blueSongImage)


        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    // pink -> odd
    inner class PinkViewHolder(private val binding: SelectsongPinkRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(response: PracticeMusicSelectResponse, position: Int) {
            binding.pinkSongNumber.text = (position + 1).toString()
            binding.pinkSongName.text = response.title
            binding.pinkSinger.text = response.artist
            // Glide를 사용해 네트워크 또는 로컬에서 이미지 로딩
            Glide.with(binding.pinkSongImage.context)
                .load(response.coverImagePath)
                .into(binding.pinkSongImage)


        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) VIEW_TYPE_BLUE else VIEW_TYPE_PINK
    }

    // 목록의 아이템 수
    override fun getItemCount(): Int {
        return info.size
    }

    // 뷰홀더가 생성되었을 때
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PINK -> {
                val viewPink = SelectsongPinkRecyclerviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PinkViewHolder(viewPink)
            }

            VIEW_TYPE_BLUE -> {
                val viewBlue = SelectsongBlueRecyclerviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BlueViewHolder(viewBlue)
            }

            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    // 뷰와 뷰홀더가 묶였을 때
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = info[position]

        when (holder) {
            is BlueViewHolder -> holder.bind(item, position)
            is PinkViewHolder -> holder.bind(item, position)
        }
    }

    // 뷰타입 상수 추가
    companion object {
        private const val VIEW_TYPE_PINK = 1
        private const val VIEW_TYPE_BLUE = 0
    }

    // item click listener
    interface RecyclerViewEvent {
        fun onItemClick(position: Int)
    }
}