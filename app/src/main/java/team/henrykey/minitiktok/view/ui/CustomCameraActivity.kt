/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

@file:Suppress("DEPRECATION")

package team.henrykey.minitiktok.view.ui


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.ExifInterface
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_custom_camera.*
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.extension.getColorCompat
import team.henrykey.minitiktok.util.PictureUtils
import team.henrykey.minitiktok.util.PictureUtils.MEDIA_TYPE_IMAGE
import team.henrykey.minitiktok.util.PictureUtils.MEDIA_TYPE_VIDEO
import team.henrykey.minitiktok.util.PictureUtils.getOutputMediaFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class CustomCameraActivity : AppCompatActivity() {

    private var mSurfaceView: SurfaceView? = null
    private var mHolder: SurfaceHolder? = null
    private var mCamera: Camera? = null

    private var CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK

    private var isRecording = false
    private var isPause = false

    private var rotationDegree = 0

    private var mWidth = 0
    private var mHeight = 0

    internal var size: Camera.Size? = null

    private var mMediaRecorder: MediaRecorder? = null
    private var videoFile: File? = null
    private var mDuration = 10000L


    private val mPicture = Camera.PictureCallback { data, camera ->
        val pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE)
        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: " + e.message)
        }

        setPictureDegreeZero(pictureFile.getAbsolutePath())

        PictureUtils.insertIntoGallery(pictureFile, this)

        mCamera?.startPreview()
    }

    private var mShutterAnim: ValueAnimator? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_custom_camera)

        mSurfaceView = findViewById(R.id.img)
        //DONE 给SurfaceHolder添加Callback
        mHolder = mSurfaceView!!.holder
        mHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mHolder!!.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.i(TAG, "surface created")
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                Log.i(TAG, "surface changed: mWidth = $width, mHeight = $height")
                mWidth = width
                mHeight = height
                releaseCameraAndPreview()
                startPreview(holder)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.i(TAG, "surface destroyed")
                releaseCameraAndPreview()
            }
        })

        shutter.setOnClickListener {
            //            mCamera?.takePicture(null, null, mPicture)
            if (isRecording) {
                mShutterAnim?.end()
                Intent(this@CustomCameraActivity, PostActivity::class.java).apply {
                    videoFile?.let {
                        this.putExtra(PostActivity.VIDEO_FILE_EXTRA, it)
                        startActivity(this)
                    }
                }
            } else {
                prepareVideoRecorder()
                shutter.recording = true
                shutter.invalidate()
                mShutterAnim = ValueAnimator.ofFloat(0f, 360f)
                    .apply {
                        interpolator = LinearInterpolator()
                        duration = mDuration
                        addUpdateListener {
                            shutter.setDegree(it.animatedValue as Float)
                        }
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                shutter.recording = false
                                shutter.setDegree(0f)
                                releaseMediaRecorder()
                            }
                        })
                        start()
                    }
            }
        }

        fifteenButton.setOnClickListener {
            if (isRecording) return@setOnClickListener
            mDuration = 15000L
            fifteenButton.paint.isFakeBoldText = true
            sixtyButton.paint.isFakeBoldText = false
            fifteenButton.setTextColor(getColorCompat(R.color.green_600))
            sixtyButton.setTextColor(getColorCompat(R.color.white))
        }

        fifteenButton.performClick()

        sixtyButton.setOnClickListener {
            if (isRecording) return@setOnClickListener
            mDuration = 60000L
            sixtyButton.paint.isFakeBoldText = true
            fifteenButton.paint.isFakeBoldText = false
            sixtyButton.setTextColor(getColorCompat(R.color.green_600))
            fifteenButton.setTextColor(getColorCompat(R.color.white))
        }

        uploadButton.setOnClickListener {
            if (!isRecording) {
                Intent(this@CustomCameraActivity, PostActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }


//        findViewById<View>(R.id.btn_picture).setOnClickListener({ v ->
//            //DONE 拍一张照片
//
//        })
//
//        val recordBtn = findViewById<Button>(R.id.btn_record)
//        val pauseBtn = findViewById<Button>(R.id.btn_record_pause)
//        recordBtn.setOnClickListener { v ->
//            //DONE 录制，第一次点击是start，第二次点击是stop
//            isPause = false
////            pauseBtn.setText(R.string.pause)
//            if (isRecording) {
//                //DONE 停止录制
////                recordBtn.setText(R.string.record)
//                pauseBtn.isEnabled = false
//                releaseMediaRecorder()
//
//            } else {
//                //DONE 录制
////                recordBtn.setText(R.string.stop)
//                if (Build.VERSION.SDK_INT >= 24) {
//                    pauseBtn.isEnabled = true
//                }
//                prepareVideoRecorder()
//            }
//        }
//
//        pauseBtn.setOnClickListener { v ->
//            if (isPause) {
//                mMediaRecorder!!.resume()
//                isPause = false
////                pauseBtn.setText(R.string.pause)
//            } else {
//                mMediaRecorder!!.pause()
//                isPause = true
////                pauseBtn.setText(R.string.resume)
//            }
//        }
//
//        findViewById<View>(R.id.btn_facing).setOnClickListener({ v ->
//            //DONE 切换前后摄像头
//            if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)
//                startPreview(mHolder)
//            } else {
//                mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
//                startPreview(mHolder)
//            }
//        })
//
//        findViewById<View>(R.id.btn_zoom_in).setOnClickListener({ v ->
//            //DONE 调焦，需要判断手机是否支持
//            val parameters = mCamera!!.parameters
//            if (parameters.isZoomSupported) {
//                val zoom = parameters.zoom + 5
//                if (zoom < parameters.maxZoom) {
//                    parameters.zoom = zoom
//                    mCamera!!.parameters = parameters
//                }
//            }
//        })
//
//        findViewById<View>(R.id.btn_zoom_out).setOnClickListener({ v ->
//            //DONE 调焦，需要判断手机是否支持
//            val parameters = mCamera!!.parameters
//            if (parameters.isZoomSupported) {
//                val zoom = parameters.zoom - 5
//                if (zoom >= 0) {
//                    parameters.zoom = zoom
//                    mCamera!!.parameters = parameters
//                }
//            }
//        })
//
//        findViewById<View>(R.id.btn_flash).setOnClickListener({ v ->
//            //DONE 监听闪光灯
//            val parameters = mCamera!!.parameters
//            if (parameters.flashMode == Camera.Parameters.FLASH_MODE_TORCH) {
//                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
//                mCamera!!.parameters = parameters
//            } else {
//                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
//                mCamera!!.parameters = parameters
//            }
//        })

    }

    fun getCamera(position: Int): Camera {
        CAMERA_TYPE = position
        if (mCamera != null) {
            releaseCameraAndPreview()
        }
        val cam = Camera.open(position)

        //DONE 摄像头添加属性，例是否自动对焦，设置旋转方向等
        rotationDegree = getCameraDisplayOrientation(position)
        cam.setDisplayOrientation(rotationDegree)

        val parameters = cam.parameters
        size = getOptimalPreviewSize(
            cam.parameters.supportedPreviewSizes,
            mWidth, mHeight
        )
        parameters.setPreviewSize(size!!.width, size!!.height)

        if (parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        }

        cam.parameters = parameters
        return cam
    }

    private fun getCameraDisplayOrientation(cameraId: Int): Int {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = windowManager.defaultDisplay
            .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = DEGREE_90
            Surface.ROTATION_180 -> degrees = DEGREE_180
            Surface.ROTATION_270 -> degrees = DEGREE_270
            else -> {
            }
        }

        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360
            result = (DEGREE_360 - result) % DEGREE_360  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360
        }
        Log.i(TAG, "orientation: $result")

        return result
    }


    private fun releaseCameraAndPreview() {
        //DONE 释放camera资源
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
    }

    private fun startPreview(holder: SurfaceHolder?) {
        //DONE 开始预览
        Log.i(TAG, "start preview")
        if (mCamera == null) {
            mCamera = getCamera(CAMERA_TYPE)
        }
        try {
            mCamera!!.setPreviewDisplay(holder)
            mCamera!!.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (mCamera!!.parameters.supportedFocusModes
                .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
        ) {
            mCamera!!.autoFocus { success, camera ->
                if (success) {
                    mCamera!!.cancelAutoFocus()
                    Log.i(TAG, "auto focus success")
                } else {
                    Log.e(TAG, "auto focus failed")
                }
            }
        }
    }

    private fun prepareVideoRecorder(): Boolean {
        //DONE 准备MediaRecorder
        Log.i(TAG, "prepare video recorder")
        isRecording = true
        mMediaRecorder = MediaRecorder().apply {
            mCamera!!.unlock()
            setCamera(mCamera)
            setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)

            setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
            videoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO)
            setOutputFile(videoFile!!.absolutePath)
            setPreviewDisplay(mHolder!!.surface)
            setOrientationHint(rotationDegree)
        }


        try {
            mMediaRecorder!!.prepare()
            mMediaRecorder!!.start()
        } catch (e: IOException) {
            releaseMediaRecorder()
            e.printStackTrace()
            return false
        }

        return true
    }


    private fun releaseMediaRecorder() {
        //DONE 释放MediaRecorder
        Log.i(TAG, "release media recorder")
        isRecording = false
        mMediaRecorder?.run {
            stop()
            reset()
            release()
        }
        mMediaRecorder = null
        videoFile?.let { PictureUtils.insertIntoGallery(it, this) }
        mCamera!!.lock()
    }


    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = h.toDouble() / w

        if (sizes == null) return null

        var optimalSize: Camera.Size? = null
        var minDiff = java.lang.Double.MAX_VALUE

        val targetHeight = Math.min(w, h)

        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - targetHeight).toDouble()
            }
        }

        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - targetHeight).toDouble()
                }
            }
        }
        return optimalSize
    }

    fun setPictureDegreeZero(path: String) {
        try {
            val exifInterface = ExifInterface(path)
            val orientation = ORIENTATION_MAP[getCameraDisplayOrientation(CAMERA_TYPE) / 90]
            exifInterface.setAttribute(
                ExifInterface.TAG_ORIENTATION,
                orientation.toString()
            )
            exifInterface.saveAttributes()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {

        private val TAG = CustomCameraActivity::class.java.simpleName


        private val DEGREE_90 = 90
        private val DEGREE_180 = 180
        private val DEGREE_270 = 270
        private val DEGREE_360 = 360

        private val ORIENTATION_MAP = intArrayOf(
            ExifInterface.ORIENTATION_NORMAL,
            ExifInterface.ORIENTATION_ROTATE_90,
            ExifInterface.ORIENTATION_ROTATE_180,
            ExifInterface.ORIENTATION_ROTATE_270
        )
    }
}
