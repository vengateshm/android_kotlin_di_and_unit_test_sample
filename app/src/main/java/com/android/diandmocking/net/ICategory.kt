package com.android.diandmocking.net

import com.android.diandmocking.model.Category
import retrofit2.Call
import retrofit2.http.GET

interface ICategory {
    @GET("/dev/categories")
    fun getCategories(): Call<ArrayList<Category>>

    @GET("/dev/categories")
    suspend fun getSimpleCategories(): ArrayList<Category>
}