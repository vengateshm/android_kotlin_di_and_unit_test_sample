package com.android.diandmocking.repo

import com.android.diandmocking.model.Category

interface CategoryRepository {
    fun getCategories(categoryResultCallBack: (isSuccess: Boolean, categories: ArrayList<Category>?, error: Exception?) -> Unit)
    suspend fun getCategories(): ArrayList<Category>
}