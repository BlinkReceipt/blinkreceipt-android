package com.blinkreceipt.ocr.ui.feature.home.data.internal

import android.content.Context
import com.blinkreceipt.ocr.ui.feature.home.data.ProductItemsFileService
import com.blinkreceipt.ocr.models.ProductItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import javax.inject.Inject

internal class ProductItemsFileServiceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val json: Json,
): ProductItemsFileService {

    override suspend fun store(
        blinkReceiptId: String,
        productItems: List<ProductItem>,
    ) {
        val filesDir = context.applicationContext.filesDir
        runCatching {
            val path = Files.createDirectories(File(filesDir, RESULTS_DIR).toPath())
            val file = File(path.toFile(), "${blinkReceiptId}.json")
            
            if(!file.exists()) {
                file.createNewFile()
            }

            withContext(Dispatchers.IO) {
                file.writeText(
                    json.encodeToString(
                        ListSerializer(ProductItem.serializer()),
                        productItems,
                    )
                )
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    override suspend fun get(
        blinkReceiptId: String,
    ): List<ProductItem> {
        val filesDir = context.applicationContext.filesDir
        val file = File(filesDir, "${RESULTS_DIR}/${blinkReceiptId}.json")

        return runCatching {
            require(file.exists()) {
                "File does not exist."
            }

            withContext(Dispatchers.IO) {
                val stringContent = file.readText()
                json.decodeFromString<List<ProductItem>>(stringContent)
            }
        }.onFailure {
            it.printStackTrace()
        }.getOrElse {
            emptyList()
        }
    }

    override suspend fun delete(blinkReceiptId: String) {
        val filesDir = context.applicationContext.filesDir
        val file = File(filesDir, "${RESULTS_DIR}/${blinkReceiptId}.json")
        runCatching {
            withContext(Dispatchers.IO) {
                require(file.delete()) {
                    "Failed to delete the file."
                }
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    companion object {
        private const val RESULTS_DIR = "results"
    }
}
