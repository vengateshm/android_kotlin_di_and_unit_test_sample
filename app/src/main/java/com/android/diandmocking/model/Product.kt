package com.android.diandmocking.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("_id")
    val id: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("filename")
    val fileName: String,
    @SerializedName(value = "height")
    val height: Int,
    @SerializedName(value = "price")
    val price: Float,
    @SerializedName(value = "rating")
    val rating: Int,
    @SerializedName(value = "title")
    val title: String,
    @SerializedName(value = "type")
    val type: Int,
    @SerializedName(value = "width")
    val width: Int
)