/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

object PictureUtils {

    private val TAG = PictureUtils::class.java.simpleName


    val MEDIA_TYPE_IMAGE = 1
    val MEDIA_TYPE_VIDEO = 2


    private val NUM_90 = 90
    private val NUM_180 = 180
    private val NUM_270 = 270

    fun convertUriToPath(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val imgPathSel = UriUtils.formatUri(context, uri)
            if (!TextUtils.isEmpty(imgPathSel)) {
                return imgPathSel
            }
        }
        val schema = uri.scheme
        if (TextUtils.isEmpty(schema) || ContentResolver.SCHEME_FILE == schema) {
            return uri.path
        }
        if ("http" == schema) {
            return uri.toString()
        }
        if (ContentResolver.SCHEME_CONTENT == schema) {
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            var cursor: Cursor? = null
            var filePath = ""
            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                if (cursor!!.moveToFirst()) {
                    filePath = cursor.getString(0)
                }
                cursor?.close()
            } catch (e: Exception) {
                // do nothing
            } finally {
                try {
                    cursor?.close()
                } catch (e2: Exception) {
                    // do nothing
                }

            }
            if (TextUtils.isEmpty(filePath)) {
                try {
                    val contentResolver = context.contentResolver
                    val selection = MediaStore.Images.Media._ID + "= ?"
                    var id = uri.lastPathSegment
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !TextUtils.isEmpty(id) && id!!.contains(":")) {
                        id = id.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    }
                    val selectionArgs = arrayOf(id)
                    cursor = contentResolver
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null)
                    if (cursor!!.moveToFirst()) {
                        filePath = cursor.getString(0)
                    }
                    cursor?.close()

                } catch (e: Exception) {
                    // do nothing
                } finally {
                    try {
                        cursor?.close()
                    } catch (e: Exception) {
                        // do nothing
                    }

                }
            }
            return filePath
        }
        return null
    }

    /**
     * Create a File for saving an image or video
     */
    fun getOutputMediaFile(type: Int): File {
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "CameraDemo")
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                throw IOException("Cannot make dir")
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = File(mediaStorageDir.path + File.separator +
                "IMG_" + timeStamp + ".jpg")
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = File(mediaStorageDir.path + File.separator +
                "VID_" + timeStamp + ".mp4")
        } else {
            throw IllegalArgumentException("Unknown type: $type")
        }
        Log.i(TAG, mediaFile.absolutePath)

        return mediaFile
    }

    fun rotateImage(bitmap: Bitmap, path: String): Bitmap {
        var srcExif: ExifInterface? = null
        try {
            srcExif = ExifInterface(path)
        } catch (e: IOException) {
            e.printStackTrace()
            return bitmap
        }

        val matrix = Matrix()
        var angle = 0
        val orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> angle = NUM_90
            ExifInterface.ORIENTATION_ROTATE_180 -> angle = NUM_180
            ExifInterface.ORIENTATION_ROTATE_270 -> angle = NUM_270
            else -> {
            }
        }
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun isPermissionsReady(activity: Activity, permissions: Array<String>?): Boolean {
        if (permissions == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        for (i in permissions.indices) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    fun reuqestPermissions(activity: Activity, permissions: Array<String>?, requestCode: Int) {
        if (permissions == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val mPermissionList = ArrayList<String>()
        for (i in permissions.indices) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i])//添加还未授予的权限
            }
        }
        if (mPermissionList.size > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }

    fun insertIntoGallery(file: File, context: Context) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val fileUri = Uri.fromFile(file)
        intent.data = fileUri
        context.sendBroadcast(intent)
    }

    fun downloadVideo(url: String, file: File): Observable<Int> {
        return Observable.create<Int> {
            if (file.exists()) {
                it.onComplete()
                return@create
            }
            val request = Request.Builder()
                .url(url)
                .build()
            val clicent = OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
                .build()
            val responseBody = clicent.newCall(request).execute().body()
            val total = responseBody!!.contentLength()

            val sink = Okio.buffer(Okio.sink(file))
            val buffer = sink.buffer()
            var currentSize = 0L
            val bufferSize = 200 * 1024L //200kb
            var len: Long
            val source = responseBody.source()
            len = source.read(buffer, bufferSize)
            while (len != -1L) {
                sink.emit()
                currentSize += len
                it.onNext((currentSize.toFloat() / total * 100).roundToInt())
                len = source.read(buffer, bufferSize)
            }
            source.close()
            sink.close()
            it.onComplete()
        }
    }
}
