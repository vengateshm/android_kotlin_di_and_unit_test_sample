package com.android.diandmocking.interactor

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.android.diandmocking.model.Category
import com.android.diandmocking.model.Product
import com.android.diandmocking.repo.CategoryRepository
import com.android.diandmocking.repo.ProductRepository
import com.android.diandmocking.util.timestamp
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@ExperimentalCoroutinesApi
class ProductUseCaseTest {
    private lateinit var systemUnderTest: ProductsUseCase

    @RelaxedMockK
    private lateinit var mockedProductRepo: ProductRepository

    @RelaxedMockK
    private lateinit var categoryRepo: CategoryRepository

    private val context = mockk<Context>()

    private val mockedSharedPreference =
        mockk<SharedPreferences>(relaxed = true)// Without annotation

    //Sample products
    private val product1 = Product(
        id = "123", description = "Raw organic brown eggs in a basket", title = "Brown eggs",
        fileName = "", height = 600, price = 28.1f, rating = 4, width = 400, type = 2
    )
    private val product2 = Product(
        id = "163",
        title = "Sweet fresh stawberry",
        description = "Sweet fresh stawberry on the wooden table",
        fileName = "",
        height = 450,
        price = 29.45f,
        rating = 4,
        width = 299,
        type = 3
    )
    private val product3 = Product(
        id = "181", title = "Asparagus", description = "Asparagus with ham on the wooden table",
        fileName = "", height = 450, price = 18.95f, rating = 4, width = 299, type = 4
    )

    //Sample categories
    private val category1 = Category(_id = "11", type = 2, description = "Dairy")
    private val category2 = Category(_id = "12", type = 3, description = "Fruits")
    private val category3 = Category(_id = "14", type = 4, description = "Vegetables")
    private val category4 = Category(_id = "15", type = 5, description = "Bakery")

    private val categorizedProducts =
        hashMapOf(
            category1.description to listOf(product1), category2.description to listOf(product2),
            category3.description to listOf(product3)
        )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        startKoin {
            loadKoinModules(
                module {
                    listOf(
                        single { mockedProductRepo },
                        single { categoryRepo }
                    )
                })
        }
        mockkStatic(PreferenceManager::class)
        every {
            PreferenceManager.getDefaultSharedPreferences(context)
        } returns mockedSharedPreference
    }

    /**
     * Test - 1
     * Features - Mocking synchronous repo method
     *
     * Verify fetching of products by mocking the repo class
     * Retrieving data from the repo is a synchronous method
     */
    @Test
    fun `verify products fetch`() {
        val mockedContext = mockk<Context>(relaxed = true)
        systemUnderTest = ProductsUseCase(mockedContext)

        runBlockingTest {
            coEvery { mockedProductRepo.getProducts() } returns arrayListOf(
                product1,
                product2,
                product3
            )
            val products = systemUnderTest.getProducts()
            assert(products.isNotEmpty())
            assert(products.contains(product1))
            assert(products.size == 3)

            //Verify that the usecase invoked getProducts method from repo to get the data
            verify(exactly = 1) {
                runBlockingTest {
                    mockedProductRepo.getProducts()
                }
            }
        }
    }

    /**
     * Test - 2
     * Features - Mocking asynchronous repo method using callback
     *
     * Verify fetching of products by mocking the repo class
     * Retrieving data from the repo is an asynchronous method
     */
    @Test
    fun `Verify fetching of products using callback`() {
        val mockedContext = mockk<Context>()
        systemUnderTest = ProductsUseCase(mockedContext)
        val productsCallback = slot<(Boolean, ArrayList<Product>?, Exception?) -> Unit>()

        coEvery { mockedProductRepo.getProducts(capture(productsCallback)) } answers {
            productsCallback.captured.invoke(true, arrayListOf(product1, product2, product3), null)
        }

        runBlockingTest {
            val products = systemUnderTest.getProductsCallback()
            products?.let { result ->
                assert(result.size == 3)
                assert(result.contains(product2))
            }
        }
    }

    /**
     * Test - 4
     * Features - shared preference mocking
     *
     * Verify fetching of timestamp by mocking shared preference
     */
    @Test
    fun `Verify last fetch timestamp`() {
        systemUnderTest = ProductsUseCase(context)
        every { mockedSharedPreference.timestamp } returns 67678L
        val timeStamp = systemUnderTest.lastFetchTime()
        assert(timeStamp == 67678L)
    }

    /**
     * Test - 5
     * Features - Mocking synchronous repo method
     *
     * Verify fetching of categories by mocking the repo class
     * Retrieving data from the repo is an synchronous method
     */
    @Test
    fun `Verify fetching of simple categories`() {
        val mockedContext = mockk<Context>(relaxed = true)
        systemUnderTest = ProductsUseCase(mockedContext)
        runBlockingTest {
            coEvery { categoryRepo.getCategories() } returns arrayListOf(
                category1,
                category2,
                category3,
                category4
            )

            val categories = systemUnderTest.getSimpleCategories()
            categories.let {
                assert(categories.isNotEmpty())
                assert(categories.contains(category1))
                assert(categories.size == 4)
            }

            verify(exactly = 1) {
                runBlockingTest {
                    categoryRepo.getCategories()
                }
            }
        }
    }

    /**
     * Test - 6
     * Features - Mocking asynchronous repo method using callback
     *
     * Verify fetching of categories by mocking the repo class
     * Retrieving data from the repo is a asynchronous method
     */
    @Test
    fun `Verify fetching of categories`() {
        val mockedContext = mockk<Context>(relaxed = true)
        systemUnderTest = ProductsUseCase(mockedContext)
        val categoriesCallback = slot<(Boolean, ArrayList<Category>?, Exception?) -> Unit>()
        coEvery {
            categoryRepo.getCategories(capture(categoriesCallback))
        } answers {
            categoriesCallback.captured.invoke(
                true, arrayListOf(
                    category1,
                    category2,
                    category3,
                    category4
                ), null
            )
        }
        runBlockingTest {
            val categories = systemUnderTest.getCategories()
            categories?.let { result ->
                assert(result.size == 4)
                assert(result.contains(category1))
            }
        }
    }


    /**
     * Test - 7
     * Features - spy - A part of the behaviour may be mocked, but any non-mocked behaviour will call the original method
     *
     * Verify fetching of products by categories by partially mocking use-case
     */
    @Test
    fun `Verify products are associated with corresponding categories`() {
        val mockedContext = mockk<Context>(relaxed = true)
        val testCoroutineDispatcher = TestCoroutineDispatcher()
        systemUnderTest = spyk(ProductsUseCase(mockedContext, testCoroutineDispatcher))
        coEvery { systemUnderTest.getProducts() } returns arrayListOf(product1, product2, product3)
        coEvery { systemUnderTest.getCategories() } returns arrayListOf(
            category1,
            category2,
            category3,
            category4
        )

        runBlockingTest {
            val categorisedProducts = systemUnderTest.getProductsByCategory()
            assert(categorisedProducts.size == 4)
        }
    }

    /**
     * Test - 8
     * Features - spy - A part of the behaviour may be mocked, but any non-mocked behaviour will call the original method
     *
     * Verify fetching of average rating by partially mocking usecase
     */
    @Test
    fun `Verify average rating of each category `() {
        val mockedContext = mockk<Context>(relaxed = true)
        val testCoroutineDispatcher = TestCoroutineDispatcher()
        systemUnderTest = spyk(ProductsUseCase(mockedContext, testCoroutineDispatcher))

        coEvery { systemUnderTest.getProducts() } returns arrayListOf(product1, product2, product3)
        coEvery { systemUnderTest.getCategories() } returns arrayListOf(
            category1,
            category2,
            category3
        )

        runBlockingTest {
            val categorisedProducts = systemUnderTest.getAverageRatingOfEachCategory()
            assert(categorisedProducts.size == 3)
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }
}