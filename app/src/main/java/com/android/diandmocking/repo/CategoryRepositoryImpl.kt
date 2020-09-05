package com.android.diandmocking.repo

import com.android.diandmocking.model.Category
import com.android.diandmocking.net.APIClient
import com.android.diandmocking.net.ICategory
import com.android.diandmocking.util.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryRepositoryImpl : CategoryRepository {
    override fun getCategories(categoryResultCallBack: (isSuccess: Boolean, categories: ArrayList<Category>?, error: Exception?) -> Unit) {
        val callAsync = APIClient(
            BASE_URL,
            isAuthorizationRequired = false
        ).createService(ICategory::class.java).getCategories()

        callAsync.enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
                categoryResultCallBack(true, response.body(), null)
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                categoryResultCallBack(false, null, Exception(t))
            }
        })
    }

    override suspend fun getCategories(): ArrayList<Category> {
        return APIClient(
            BASE_URL,
            isAuthorizationRequired = false
        ).createService(ICategory::class.java).getSimpleCategories()
    }
}