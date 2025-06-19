package com.example.danzle.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.danzle.data.remote.response.auth.MusicSelectResponse
import com.example.danzle.databinding.SelectsongBlueRecyclerviewBinding
import com.example.danzle.databinding.SelectsongPinkRecyclerviewBinding

class ChallengeMusicSelectRVAdapter(
    private val info: ArrayList<MusicSelectResponse>,
    private val listener: ChallengeRecyclerViewEvent
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class BlueViewHolder(private val binding: SelectsongBlueRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(response: MusicSelectResponse, position: Int) {
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

        fun bind(response: MusicSelectResponse, position: Int) {
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
            is BlueViewHolder -> holder.bind(item, position)
            is PinkViewHolder -> holder.bind(item, position)
        }
    }

    override fun getItemCount(): Int = info.size

    companion object {
        private const val VIEW_TYPE_PINK = 1
        private const val VIEW_TYPE_BLUE = 0
    }

    interface ChallengeRecyclerViewEvent {
        fun onItemClick(position: Int)
    }
}