package com.android.diandmocking.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.diandmocking.interactor.ProductsUseCase
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class ProductsViewModel(private val context: Context) : ViewModel(), KoinComponent {

    private val productUseCase by inject<ProductsUseCase> { parametersOf(context) }

    fun getCategorisedProducts() {
        viewModelScope.launch {
            val result = productUseCase.getProductsByCategory()
            println(result)
        }
    }
}