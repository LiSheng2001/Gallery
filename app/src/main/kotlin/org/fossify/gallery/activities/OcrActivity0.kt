//package org.fossify.gallery.activities
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.Log
//import android.widget.ImageView
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import kotlinx.coroutines.*
//import org.fossify.gallery.R
//import org.fossify.gallery.models.Medium
//import java.io.File
//import java.io.FileNotFoundException
//import java.io.InputStream
//
//class MyGalleryActivity : SimpleActivity() {
//
//    private val TAG = "AutoOCRActivity"
//    private val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 101
//    private lateinit var imageView: ImageView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_my_gallery)
//
//        imageView = findViewById(R.id.my_image_view)
//
//        // 检查权限
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_DENIED
//        ) {
//            // 请求权限
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                READ_EXTERNAL_STORAGE_PERMISSION_CODE
//            )
//        } else {
//            // 已经授权，加载媒体文件
//            loadMediaFiles()
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 权限已授予，加载媒体文件
//                loadMediaFiles()
//            } else {
//                // 权限被拒绝，显示提示信息
//                Log.e(TAG, "读取外部存储权限被拒绝")
//            }
//        }
//    }
//
//    private fun loadMediaFiles() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val mediaList = getAllMediaFiles()
//
//            withContext(Dispatchers.Main) {
//                mediaList.forEach { medium ->
//                    loadBitmap(medium)
//                }
//            }
//        }
//    }
//
//    private suspend fun getAllMediaFiles(): MutableList<Medium> = withContext(Dispatchers.IO) {
//        val mediaList = mutableListOf<Medium>()
//        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(
//            MediaStore.Images.Media._ID,
//            MediaStore.Images.Media.DISPLAY_NAME,
//            MediaStore.Images.Media.DATA
//        )
//
//        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
//            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
//            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//
//            while (cursor.moveToNext()) {
//                val id = cursor.getLong(idColumn)
//                val name = cursor.getString(nameColumn)
//                val path = cursor.getString(dataColumn)
//
//                val medium = Medium(id, name, path, File(path).parent ?: "", 0, 0, 0, 0, 0, false, 0L, 0L)
//                mediaList.add(medium)
//            }
//        }
//        return@withContext mediaList
//    }
//
//    private fun loadBitmap(medium: Medium) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val bitmap = getBitmapFromPath(medium.path)
//
//                withContext(Dispatchers.Main) {
//                    imageView.setImageBitmap(bitmap)
//                    Log.d(TAG, "加载图片: ${medium.name}")
//                    // 延迟一段时间，显示下一张图片
//                    delay(2000)
//                }
//            } catch (e: FileNotFoundException) {
//                Log.e(TAG, "文件未找到: ${medium.path}")
//            }
//        }
//    }
//
//    @Throws(FileNotFoundException::class)
//    private fun getBitmapFromPath(path: String): Bitmap? {
//        var inputStream: InputStream? = null
//        try {
//            inputStream = contentResolver.openInputStream(Uri.fromFile(File(path)))
//            return BitmapFactory.decodeStream(inputStream)
//        } finally {
//            inputStream?.close()
//        }
//    }
//}
