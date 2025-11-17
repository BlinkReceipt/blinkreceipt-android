package com.blinkreceipt.ocr.di

import com.blinkreceipt.ocr.ui.feature.home.data.ProductItemsFileService
import com.blinkreceipt.ocr.ui.feature.home.data.internal.ProductItemsFileServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    internal abstract fun bindProductItemsFileService(
        productItemsFileService: ProductItemsFileServiceImpl
    ): ProductItemsFileService

    companion object {
        @Provides
        @Singleton
        fun provideJson(): Json = Json
    }
}