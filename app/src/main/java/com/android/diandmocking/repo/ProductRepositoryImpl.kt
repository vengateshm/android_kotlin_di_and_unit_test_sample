package com.android.diandmocking.repo

import com.android.diandmocking.model.Category
import com.android.diandmocking.model.Product
import com.android.diandmocking.net.APIClient
import com.android.diandmocking.net.IProducts
import com.android.diandmocking.util.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepositoryImpl : ProductRepository {
    override suspend fun getProducts(): ArrayList<Product> {
        return APIClient(
            BASE_URL,
            isAuthorizationRequired = false
        ).createService(IProducts::class.java).getProducts()
    }

    override fun getProducts(productResultCallBack: (isSuccess: Boolean, categories: ArrayList<Product>?, error: Exception?) -> Unit) {
        val callAsync = APIClient(
            BASE_URL,
            isAuthorizationRequired = false
        ).createService(IProducts::class.java).getProductsCallback()
        callAsync.enqueue(object : Callback<ArrayList<Product>> {
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                productResultCallBack(true, response.body(), null)
            }

            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                productResultCallBack(false, null, Exception(t))
            }
        })
    }
}