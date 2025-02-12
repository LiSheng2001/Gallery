package org.fossify.gallery.activities

import android.os.Bundle
import org.fossify.commons.extensions.viewBinding
import org.fossify.gallery.databinding.ActivityOcrBinding
import org.fossify.commons.helpers.*
import org.fossify.gallery.databases.GalleryDatabase
import org.fossify.gallery.helpers.OcrHelper
import org.fossify.gallery.interfaces.MediumDao
import kotlinx.coroutines.*
import org.fossify.commons.extensions.updateTextColors
import java.util.Locale

class OcrActivity : SimpleActivity() {
    private val binding by viewBinding(ActivityOcrBinding::inflate)
    private val mediaDB: MediumDao = GalleryDatabase.getInstance(this).MediumDao()
    private var ocrJob: Job? = null
    private var ocrHelper: OcrHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // 绑定布局文件

        updateMaterialActivityViews(binding.ocrSettingsCoordinator, binding.autoOcrHolder, useTransparentNavigation = true, useTopSearchMenu = false)
        setupMaterialScrollListener(binding.ocrSettingsNestedScrollview, binding.ocrSettingsToolbar)

        // 添加绑定
        setupSelectImageRatio()
        setupStartOCR()
        setupCancelOCR()
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(binding.ocrSettingsToolbar, NavigationIcon.Arrow)
        // 这样即可随主题更换颜色
        updateTextColors(binding.autoOcrHolder)
    }


    private fun setupSelectImageRatio() {
        // 控制显示正则表达式的输入框
        binding.ocrScope.setOnCheckedChangeListener{ _, checkedId ->
            if (checkedId == binding.ocrSelected.id) { // 当选中“仅符合规则的图像”
                binding.ocrRules.visibility = android.view.View.VISIBLE
            } else {
                binding.ocrRules.visibility = android.view.View.GONE
            }
        }
    }

    private fun startOCR() {
        // 启动一个协程在后台执行 OCR
        ocrJob = CoroutineScope(Dispatchers.Main).launch { // 在 Main 线程启动协程
            // 初始化OCR识别实例
            ocrHelper = OcrHelper(this@OcrActivity)

            // 获取数据库中的所有符合要求图像的路径
            val fullPaths: List<String> = if (binding.ocrScope.checkedRadioButtonId == binding.ocrAll.id){
                withContext(Dispatchers.IO) {
                    mediaDB.getAllImagesNotHaveCaption(captionType = "ml_kit_ocr").map { it.path }
                }
            }else{
                // 获取正则表达式
                val regexPattern = binding.ocrRules.text.toString()
                // 检查正则表达式是否合法
                val regex: Regex = try {
                    Regex(regexPattern)
                } catch (e: Exception) {
                    runOnUiThread {
                        binding.ocrStatus.text = String.format(Locale.getDefault(), "无效的正则表达式: %s", e.message)
                    }
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    mediaDB.getAllImagesNotHaveCaption(captionType = "ml_kit_ocr")
                        .filter { regex.containsMatchIn(it.name) } // 过滤符合正则的文件名
                        .map { it.path }
                }
            }

            // 记录开始时间以修正处理速度
            val startTime = System.currentTimeMillis()

            // 运行OCR识别
            ocrHelper?.let { helper ->
                helper.recognizeBatch(fullPaths,
                    onProgress = { completed, total ->
                        // 计算实时处理速度
                        val currentTime = System.currentTimeMillis()
                        val elapsedTime = (currentTime - startTime) / 1000.0 // 转换为秒，避免除以 0

                        val speed = if (elapsedTime > 0) completed / elapsedTime else 0.0 // 计算每秒处理的图片数

                        // 在UI线程更新
                        runOnUiThread {
                            binding.ocrProgress.setProgress(completed * 100 / total)
                            binding.ocrStatus.text = String.format(Locale.getDefault(), "已完成: %d / %d，处理速度: %.2f 张/秒", completed, total, speed)
                        }
                    },
                    onComplete = { canceled, completed ->
                        // 在UI线程中更新
                        runOnUiThread {
                            if (canceled) {
                                binding.ocrStatus.text = String.format(Locale.getDefault(), "已取消，本次处理: %d 张图像", completed)
                            } else {
                                binding.ocrStatus.text = String.format(Locale.getDefault(), "已完成，本次处理: %d 张图像", completed)
                            }
                        }
                    }
                )
            }
        }
    }

    private fun setupStartOCR() {
        binding.ocrStart.setOnClickListener { startOCR() }
    }

    private fun setupCancelOCR() {
        binding.ocrCancel.setOnClickListener {
            ocrHelper?.cancelBatch()
        }
    }
}
