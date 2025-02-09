package org.fossify.gallery.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.fossify.gallery.R
import org.fossify.gallery.databases.GalleryDatabase
import org.fossify.gallery.extensions.getBitmapFromPath
import org.fossify.gallery.helpers.OcrHelper
import org.fossify.gallery.models.Medium

class OcrActivity : AppCompatActivity() {

    private lateinit var mStatusTextView: TextView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mOcrAllRadioButton: RadioButton
    private lateinit var mOcrSelectedRadioButton: RadioButton
    private lateinit var mStartButton: Button
    private lateinit var mMediums: List<Medium>
    private lateinit var mMediumDao: MediumDao
    private lateinit var mOcrHelper: OcrHelper
    private var mJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        mStatusTextView = findViewById(R.id.ocr_status)
        mProgressBar = findViewById(R.id.ocr_progress)
        mOcrAllRadioButton = findViewById(R.id.ocr_all)
        mOcrSelectedRadioButton = findViewById(R.id.ocr_selected)
        mStartButton = findViewById(R.id.ocr_start)

        mMediumDao = GalleryDatabase.getInstance(applicationContext).MediumDao()
        mOcrHelper = OcrHelper(applicationContext)

        mStartButton.setOnClickListener {
            startOcr()
        }
    }

    private fun startOcr() {
        val scope = if (mOcrAllRadioButton.isChecked) {
            "all"
        } else {
            "selected"
        }

        mJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                mStatusTextView.text = "正在加载图片..."
                mProgressBar.progress = 0
            }

            mMediums = mMediumDao.getAll()
            val total = mMediums.size
            var processed = 0

            for (medium in mMediums) {
                if (medium.caption.isNullOrEmpty()) {
                    try {
                        withContext(Dispatchers.Main) {
                            mStatusTextView.text = "正在识别：${medium.name}"
                        }
                        val bitmap = applicationContext.getBitmapFromPath(medium.path)
                        val text = mOcrHelper.recognizeText(bitmap)
                        medium.caption = text
                        mMediumDao.update(medium)
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            mStatusTextView.text = "识别失败：${medium.name}"
                        }
                    }
                }

                processed++
                val progress = (processed * 100 / total).toInt()
                withContext(Dispatchers.Main) {
                    mProgressBar.progress = progress
                }
            }

            withContext(Dispatchers.Main) {
                mStatusTextView.text = "识别完成"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob?.cancel()
    }
}