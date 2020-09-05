package com.android.diandmocking.net

import com.android.diandmocking.model.Product
import retrofit2.Call
import retrofit2.http.GET

interface IProducts {
    @GET("/dev/products")
    suspend fun getProducts(): ArrayList<Product>

    @GET("/dev/products")
    fun getProductsCallback(): Call<ArrayList<Product>>
}