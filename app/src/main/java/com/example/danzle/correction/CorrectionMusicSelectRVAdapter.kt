package com.example.danzle.correction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.danzle.data.remote.response.auth.CorrectionMusicSelectResponse
import com.example.danzle.databinding.SelectsongBlueRecyclerviewBinding
import com.example.danzle.databinding.SelectsongPinkRecyclerviewBinding

class CorrectionMusicSelectRVAdapter(
    private val info: ArrayList<CorrectionMusicSelectResponse>,
    private val listener: RecyclerViewEvent
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class BlueViewHolder(private val binding: SelectsongBlueRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(response: CorrectionMusicSelectResponse, position: Int) {
            binding.blueSongNumber.text = (position + 1).toString()
            binding.blueSongName.text = response.title
            binding.blueSinger.text = response.artist

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

    inner class PinkViewHolder(private val binding: SelectsongPinkRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(response: CorrectionMusicSelectResponse, position: Int) {
            binding.pinkSongNumber.text = (position + 1).toString()
            binding.pinkSongName.text = response.title
            binding.pinkSinger.text = response.artist

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

    override fun getItemCount(): Int {
        return info.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = info[position]

        when (holder) {
            is CorrectionMusicSelectRVAdapter.BlueViewHolder -> holder.bind(item, position)
            is CorrectionMusicSelectRVAdapter.PinkViewHolder -> holder.bind(item, position)
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