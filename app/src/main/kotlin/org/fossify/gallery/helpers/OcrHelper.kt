package org.fossify.gallery.helpers

import android.content.Context
import java.io.IOException
import android.net.Uri
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import java.util.concurrent.ConcurrentLinkedQueue
import org.fossify.gallery.interfaces.MediumDao
import org.fossify.gallery.databases.GalleryDatabase

class OcrHelper(private val context: Context) {
    // 单独初始化recognizer避免重复初始化
    private val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    // 用于控制批量任务中断
    private var batchJob: Job? = null
    private var canceled: Boolean = false

    // 缓存 OCR 结果的线程安全队列，格式是[[fullPath_1, caption_1], ..., [fullPath_n, caption_n]]
    private val resultQueue = ConcurrentLinkedQueue<Pair<String, String>>()

    // 管理数据库
    private val mediaDB: MediumDao = GalleryDatabase.getInstance(context).MediumDao()


    suspend fun recognizeText(fullPath: String): String = withContext(Dispatchers.IO) {
        val uri = if (fullPath.startsWith("content://") || fullPath.startsWith("file://")) {
            Uri.parse(fullPath)
        } else {
            Uri.fromFile(File(fullPath)) // 处理普通文件路径
        }

        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, uri)
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext "" // 文件读取失败，返回空字符串
        }

        return@withContext suspendCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // 返回时额外加个符号，在不影响检索的同时跳过已经OCR但无文本的项目
                    continuation.resume("✅" + visionText.text)
                }
                .addOnFailureListener { e ->
                    continuation.resume("")
                }
        }
    }

    // 🚀 批量 OCR，支持任务取消
    fun recognizeBatch(fullPaths: List<String>,
                       onProgress: (Int, Int) -> Unit,
                       onComplete: (Boolean, Int) -> Unit) {
        batchJob = CoroutineScope(Dispatchers.IO).launch {
            val total = fullPaths.size
            var completed = 0
            val batchSize = 20 // 每批次写入数据库的结果数量

            // debug
            val k = withContext(Dispatchers.IO) {
                mediaDB.getAllImages()
            }
            k

            try {
                // 并发启动 OCR 任务
                val deferredList = fullPaths.map { fullPath ->
                    async {
                        val caption = recognizeText(fullPath)
                        if (caption != "") {
                            // 仅发送正确识别的caption
                            resultQueue.offer(fullPath to caption)
                        }
                        completed++
                        onProgress(completed, total)
                    }
                }

                // 我不想等所有任务做完，我想有做完一个之后立即放入以[fullPath, caption]的形式放入resultQueue
                while (isActive) {
                    // 检查 OCR 任务是否全部完成
                    if (deferredList.all { it.isCompleted }) {
                        break
                    }

                    // 如果队列中结果数量达到阈值，批量写入数据库
                    if (resultQueue.size >= batchSize) {
                        val batchResults = mutableListOf<Pair<String, String>>()
                        repeat(batchSize) {
                            resultQueue.poll()?.let { batchResults.add(it) }
                        }

                        // 批量写入
                        withContext(Dispatchers.IO) {
                            mediaDB.updateCaptions(batchResults)
                        }
                    }

                    delay(500) // 每 500ms 轮询一次
                }
            } catch (e: CancellationException) {
                canceled = true
            } finally {
                // 处理剩余未写入的结果
                val remainingResults = mutableListOf<Pair<String, String>>()
                while (resultQueue.isNotEmpty()) {
                    resultQueue.poll()?.let { remainingResults.add(it) }
                }
                if (remainingResults.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        mediaDB.updateCaptions(remainingResults)
                    }
                }

                // 无论任务是正常完成还是被取消，都会调用 onComplete
                onComplete(canceled, completed)

                // 关闭连接
                close()
            }

            // debug
            val c = withContext(Dispatchers.IO) {
                mediaDB.getAllImages()
            }
            c
        }
    }

    // ✅ 取消批量 OCR 任务
    fun cancelBatch() {
        batchJob?.cancel()  // 取消协程
        batchJob = null
    }

    // 释放OCR模型
    fun close() {
        recognizer.close()

    }

    // 获取结果队列 (如果需要)
    fun getResultQueue(): ConcurrentLinkedQueue<Pair<String, String>> {
        return resultQueue
    }
}
