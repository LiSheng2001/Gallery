package org.fossify.gallery.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.fossify.gallery.databases.GalleryDatabase
import org.fossify.gallery.helpers.OcrHelper
import org.fossify.gallery.models.Medium

class OcrActivity : AppCompatActivity() {

    private lateinit var mMediums: List<Medium>
    private lateinit var mMediumDao: MediumDao
    private lateinit var mOcrHelper: OcrHelper
    private var mJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mMediumDao = GalleryDatabase.getInstance(applicationContext).MediumDao()
        mOcrHelper = OcrHelper(applicationContext)

        mJob = CoroutineScope(Dispatchers.IO).launch {
            mMediums = mMediumDao.getAll()

            for (medium in mMediums) {
                if (medium.caption.isNullOrEmpty()) {
                    val bitmap = applicationContext.getBitmapFromPath(medium.path)
                    val text = mOcrHelper.recognizeText(bitmap)
                    medium.caption = text
                    mMediumDao.update(medium)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob?.cancel()
    }
}