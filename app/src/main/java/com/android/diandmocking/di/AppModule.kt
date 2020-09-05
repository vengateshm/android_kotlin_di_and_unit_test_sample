package com.android.diandmocking.di

import android.content.Context
import com.android.diandmocking.interactor.ProductsUseCase
import com.android.diandmocking.repo.CategoryRepository
import com.android.diandmocking.repo.CategoryRepositoryImpl
import com.android.diandmocking.repo.ProductRepository
import com.android.diandmocking.repo.ProductRepositoryImpl
import com.android.diandmocking.viewmodel.ProductsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { (context: Context) -> ProductsViewModel(context) }
}

val domainModules = module {
    factory { (context: Context) -> ProductsUseCase(context) }
}

val repo = module {
    single<ProductRepository> { ProductRepositoryImpl() }
    single<CategoryRepository> { CategoryRepositoryImpl() }
}