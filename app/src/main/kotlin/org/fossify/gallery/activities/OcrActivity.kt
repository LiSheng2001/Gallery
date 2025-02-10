// package org.fossify.gallery.activities

// import android.os.Bundle
// import android.widget.Button
// import android.widget.ProgressBar
// import android.widget.*
// import androidx.appcompat.app.AppCompatActivity
// import kotlinx.coroutines.*
// import org.fossify.gallery.R
// import org.fossify.gallery.databases.GalleryDatabase
// import org.fossify.gallery.extensions.getBitmapFromPath
// import org.fossify.gallery.helpers.OcrHelper
// import org.fossify.gallery.models.Medium

// class OcrActivity : AppCompatActivity() {

//     private lateinit var mStatusTextView: TextView
//     private lateinit var mProgressBar: ProgressBar
//     private lateinit var mOcrAllRadioButton: RadioButton
//     private lateinit var mOcrSelectedRadioButton: RadioButton
//     private lateinit var mStartButton: Button
//     private lateinit var mResultTextView: TextView
//     private lateinit var mMediums: List<Medium>
//     private lateinit var mMediumDao: MediumDao
//     private lateinit var mOcrHelper: OcrHelper
//     private var mJob: Job? = null
//     private lateinit var mCancelButton: Button
//     private lateinit var mOcrCaptionEditText: EditText
//     private lateinit var mSaveButton: Button
//     private var mCurrentMedium: Medium? = null

//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         setContentView(R.layout.activity_ocr)

//         mStatusTextView = findViewById(R.id.ocr_status)
//         mProgressBar = findViewById(R.id.ocr_progress)
//         mOcrAllRadioButton = findViewById(R.id.ocr_all)
//         mOcrSelectedRadioButton = findViewById(R.id.ocr_selected)
//         mStartButton = findViewById(R.id.ocr_start)
//         mResultTextView = findViewById(R.id.ocr_result)
//         mCancelButton = findViewById(R.id.ocr_cancel)
//         mOcrCaptionEditText = findViewById(R.id.ocr_caption)

//         mMediumDao = GalleryDatabase.getInstance(applicationContext).MediumDao()
//         mOcrHelper = OcrHelper(applicationContext)

//         mStartButton.setOnClickListener {
//             startOcr()
//         }

//         mCancelButton.setOnClickListener {
//             cancelOcr()
//         }

//         mSaveButton = Button(this)
//         mSaveButton.text = "保存"
//         val layoutParams = LinearLayout.LayoutParams(
//             LinearLayout.LayoutParams.WRAP_CONTENT,
//             LinearLayout.LayoutParams.WRAP_CONTENT
//         )
//         mSaveButton.layoutParams = layoutParams

//         (findViewById(R.id.ocr_scope) as LinearLayout).addView(mSaveButton)

//         mSaveButton.setOnClickListener {
//             saveCaption()
//         }
//     }

//     private fun startOcr() {
//         mStartButton.visibility = View.GONE
//         mCancelButton.visibility = View.VISIBLE

//         val scope = if (mOcrAllRadioButton.isChecked) {
//             "all"
//         } else {
//             "selected"
//         }

//         mJob = CoroutineScope(Dispatchers.IO).launch {
//             withContext(Dispatchers.Main) {
//                 mStatusTextView.text = "正在加载图片..."
//                 mProgressBar.progress = 0
//             }

//             mMediums = mMediumDao.getAll()
//             val total = mMediums.size
//             var processed = 0

//             val mediums = if (scope == "all") {
//                 mMediums
//             } else {
//                 mMediums.filter { it.isFavorite } // 假设 isFavorite 表示选中
//             }

//             for (medium in mediums) {
//                 if (!medium.caption.isNullOrEmpty()) {
//                     processed++
//                     continue
//                 }

//                 mCurrentMedium = medium

//                 try {
//                     withContext(Dispatchers.Main) {
//                         mStatusTextView.text = "正在识别：${medium.name}"
//                     }
//                     val bitmap = applicationContext.getBitmapFromPath(medium.path)
//                     val text = mOcrHelper.recognizeText(bitmap)
//                     medium.caption = text

//                     withContext(Dispatchers.Main) {
//                         mOcrCaptionEditText.setText(text)
//                     }
//                 } catch (e: Exception) {
//                     withContext(Dispatchers.Main) {
//                         mStatusTextView.text = "识别失败：${medium.name}"
//                     }
//                 }

//                 processed++
//                 val progress = (processed * 100 / total).toInt()
//                 withContext(Dispatchers.Main) {
//                     mProgressBar.progress = progress
//                 }

//                 if (mJob?.isCancelled == true) {
//                     break
//                 }
//             }

//             withContext(Dispatchers.Main) {
//                 mStatusTextView.text = "识别完成"
//                 mStartButton.visibility = View.VISIBLE
//                 mCancelButton.visibility = View.GONE
//             }
//         }
//     }

//     private fun cancelOcr() {
//         mJob?.cancel()
//         mStartButton.visibility = View.VISIBLE
//         mCancelButton.visibility = View.GONE
//         mStatusTextView.text = "识别已取消"
//     }

//     private fun saveCaption() {
//         val caption = mOcrCaptionEditText.text.toString()
//         mCurrentMedium?.let {
//             it.caption = caption
//             mMediumDao.update(it)
//         }
//     }

//     override fun onDestroy() {
//         super.onDestroy()
//         mJob?.cancel()
//     }
// }