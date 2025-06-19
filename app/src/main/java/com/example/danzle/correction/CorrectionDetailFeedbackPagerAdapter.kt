package com.example.danzle.correction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.danzle.data.remote.response.auth.CorrectionFeedbackDetailResponse
import com.example.danzle.databinding.FeedbackPageBinding

class CorrectionDetailFeedbackPagerAdapter(
    private val items: List<CorrectionFeedbackDetailResponse>
) : RecyclerView.Adapter<CorrectionDetailFeedbackPagerAdapter.FeedbackViewHolder>() {

    inner class FeedbackViewHolder(private val binding: FeedbackPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CorrectionFeedbackDetailResponse) {

            Glide.with(binding.root.context)
                .load(item.userImageUrl)
                .into(binding.feedbackImage)

            val formattedText = item.feedbacks.flatMap { feedback ->
                // 앞의 "1. " 등 3글자 제거
                val cleaned = if (feedback.length > 3) feedback.substring(3) else feedback
                cleaned.split(Regex("\\."))
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            }.mapIndexed { index, sentence ->
                "${index + 1}. $sentence."
            }.joinToString("\n\n")

            binding.feedbackContent.text = formattedText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FeedbackPageBinding.inflate(inflater, parent, false)
        return FeedbackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}