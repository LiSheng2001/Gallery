package org.fossify.gallery.activities

import android.os.Bundle
import org.fossify.commons.extensions.viewBinding
import org.fossify.gallery.databinding.ActivityOcrBinding
import org.fossify.commons.helpers.*
import org.fossify.gallery.databases.GalleryDatabase
import org.fossify.gallery.helpers.OcrHelper
import org.fossify.gallery.interfaces.MediumDao
import org.fossify.gallery.models.Medium
import kotlinx.coroutines.*
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
            val fullPaths = withContext(Dispatchers.IO) {
                mediaDB.getAllImagesNotHaveCaption(captionType = "ml_kit_ocr").map { it.path }
            }

            // 运行OCR识别
            ocrHelper?.let { helper ->
                helper.recognizeBatch(fullPaths,
                    onProgress = { completed, total ->
                        // 在UI线程更新
                        runOnUiThread {
                            binding.ocrProgress.setProgress(completed / total)
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
            ocrHelper?.let { helper ->
                helper.cancelBatch()
            }
        }
    }
}
