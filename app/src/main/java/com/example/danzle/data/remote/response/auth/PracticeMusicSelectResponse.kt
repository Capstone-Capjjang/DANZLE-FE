package com.example.danzle.data.remote.response.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PracticeMusicSelectResponse (
    val id: Long?,
    val title: String?,
    val artist: String?,
    val coverImagePath: String?
) : Parcelable
