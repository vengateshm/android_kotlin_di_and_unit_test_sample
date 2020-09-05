package com.android.diandmocking.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("_id")
    val _id: String,
    @SerializedName("id")
    val type: Int,
    @SerializedName("description")
    val description: String
)