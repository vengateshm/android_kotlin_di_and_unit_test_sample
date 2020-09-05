package com.android.diandmocking.interactor

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.android.diandmocking.model.Category
import com.android.diandmocking.model.Product
import com.android.diandmocking.repo.CategoryRepository
import com.android.diandmocking.repo.ProductRepository
import com.android.diandmocking.util.timestamp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProductsUseCase(val context: Context, val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    KoinComponent {

    private val productRepo by inject<ProductRepository>()
    private val categoryRepo by inject<CategoryRepository>()

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    suspend fun getProducts(): ArrayList<Product> {
        val products =  productRepo.getProducts()
        preferences.timestamp = System.currentTimeMillis()
        return products
    }

    fun lastFetchTime() = preferences.timestamp

    suspend fun getProductsCallback(): ArrayList<Product>? {
        return suspendCoroutine { continuation ->
            productRepo.getProducts { isSuccess, products, exception ->
                if (isSuccess && exception == null) {
                    continuation.resume(products)
                } else {
                    continuation.resumeWithException(Exception(exception))
                }
            }
        }
    }

    suspend fun getCategories(): ArrayList<Category>? {
        return suspendCoroutine { continuation ->
            categoryRepo.getCategories { isSuccess, categories, exception ->
                if (isSuccess && exception == null) {
                    continuation.resume(categories)
                } else {
                    continuation.resumeWithException(Exception(exception))
                }
            }
        }
    }

    suspend fun getSimpleCategories(): ArrayList<Category> {
        return categoryRepo.getCategories()
    }

    suspend fun getProductsByCategory(): HashMap<String, List<Product>> {
        return coroutineScope {
            withContext(dispatcher) {
                val categorisedProducts = hashMapOf<String, List<Product>>()
                val products = getProducts()
                val categories = getCategories()
                categories?.forEach { category ->
                    products.filter { product -> product.type == category.type }.apply {
                        categorisedProducts[category.description] = this
                    }
                }
                categorisedProducts
            }
        }
    }

    suspend fun getAverageRatingOfEachCategory(): HashMap<String, Double> {
        return coroutineScope {
            withContext(dispatcher) {
                val ratingsForCategories = hashMapOf<String, Double>()
                val categorisedProducts = getProductsByCategory()
                categorisedProducts.keys.forEach{category ->
                    var totalRating = 0
                    categorisedProducts[category]?.forEach { product ->
                        totalRating += product.rating
                    }
                    val avgRating = (totalRating/categorisedProducts[category]!!.size).toDouble()
                    ratingsForCategories[category] = avgRating
                }
                ratingsForCategories
            }
        }
    }
}