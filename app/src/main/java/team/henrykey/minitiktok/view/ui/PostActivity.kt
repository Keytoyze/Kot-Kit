/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_post.*
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.util.PictureUtils
import team.henrykey.minitiktok.viewmodel.PostViewModel
import java.io.File

class PostActivity : AppCompatActivity() {
    private var videoFile: File? = null
    private var selectedImg: Uri? = null
    private var selectedVideo: Uri? = null
    private var previewBitmap: MutableLiveData<Bitmap> = MutableLiveData()

    private lateinit var viewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this@PostActivity)[PostViewModel::class.java]

        previewBitmap.observe(this@PostActivity, Observer<Bitmap> { bitmap ->
            bitmap?.also {
                postImg.setImageBitmap(it)
            }
        })

        intent.extras?.run {
            videoFile = get(VIDEO_FILE_EXTRA) as File
            videoFile?.run {
                vidBtn.visibility = View.INVISIBLE
                selectedVideo = toUri()
                previewBitmap.value = ThumbnailUtils.createVideoThumbnail(
                    this.absolutePath,
                    MediaStore.Images.Thumbnails.MINI_KIND
                )
                val bitmap = ThumbnailUtils.createVideoThumbnail(
                    this.absolutePath,
                    MediaStore.Images.Thumbnails.FULL_SCREEN_KIND
                )
                val file = PictureUtils.saveTempBitmap(this@PostActivity, bitmap)
                selectedImg = file.toUri()
            }
        } ?: run {
            vidBtn.visibility = View.VISIBLE
        }

        posCoverText.setOnClickListener {
            chooseImage()
        }

        vidBtn.setOnClickListener {
            chooseVideo()
        }

        postBtn.setOnClickListener {
            selectedImg?.also { img ->
                selectedVideo?.also { vid ->
                    progressBar.visibility = View.VISIBLE
                    viewModel.postVideo(this@PostActivity, img, vid) {
                        val postStatus = when (it) {
                            true -> "Succeeded"
                            false -> "failed"
                        }

                        Toast.makeText(this@PostActivity, "Post $postStatus", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.INVISIBLE
                        finish()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                data?.run {
                    selectedImg = getData()
                    updatePreview()
                }
            } else if (requestCode == PICK_VIDEO) {
                data?.run {
                    selectedVideo = getData()
                }
            }
        }
    }

    private fun updatePreview() {
        val targetH = postImg.height
        val targetW = postImg.width

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        val path = PictureUtils.convertUriToPath(this@PostActivity, selectedImg)
        BitmapFactory.decodeFile(path, options)
        val sample = PictureUtils.calculateInSampleSize(options, targetW, targetH)
        options.apply {
            inJustDecodeBounds = false
            inSampleSize = sample
        }

        BitmapFactory.decodeFile(path, options)?.also { bitmap ->
            val exif = ExifInterface(path)
            val matrix = Matrix()
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_NORMAL -> matrix.postRotate(0f)
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }
            previewBitmap.value = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE
        )
    }

    fun chooseVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO)
    }

    companion object {
        const val VIDEO_FILE_EXTRA = "intent_video_file"
        const val PICK_IMAGE = 1
        const val PICK_VIDEO = 2
    }
}
