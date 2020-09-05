package com.android.diandmocking.repo

import com.android.diandmocking.model.Product

interface ProductRepository {
    suspend fun getProducts(): ArrayList<Product>
    fun getProducts(productResultCallBack: (isSuccess: Boolean, categories: ArrayList<Product>?, error: Exception?) -> Unit)
}