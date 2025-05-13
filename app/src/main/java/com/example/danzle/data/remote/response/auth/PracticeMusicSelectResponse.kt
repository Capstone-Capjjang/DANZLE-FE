package com.example.danzle.data.remote.response.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PracticeMusicSelectResponse (
    @SerializedName("id")
    val songId: Long?,
    val title: String?,
    val artist: String?,
    val coverImagePath: String?
) : Parcelable
