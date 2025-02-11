package org.fossify.gallery.activities

import android.os.Bundle
import org.fossify.commons.extensions.viewBinding
import org.fossify.gallery.databinding.ActivityOcrBinding
import org.fossify.commons.helpers.*

class OcrActivity : SimpleActivity() {
    private val binding by viewBinding(ActivityOcrBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // 绑定布局文件

        updateMaterialActivityViews(binding.ocrSettingsCoordinator, binding.autoOcrHolder, useTransparentNavigation = true, useTopSearchMenu = false)
        setupMaterialScrollListener(binding.ocrSettingsNestedScrollview, binding.ocrSettingsToolbar)

        // 添加绑定
        setupSelectImageRatio()
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
}
