/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.player

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.net.Uri

import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

import tv.danmaku.ijk.media.player.misc.IMediaDataSource

class RawDataSourceProvider(private var mDescriptor: AssetFileDescriptor?) : IMediaDataSource {

    private var mMediaBytes: ByteArray? = null

    @Throws(IOException::class)
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (position + 1 >= mMediaBytes!!.size) {
            return -1
        }

        var length: Int
        if (position + size < mMediaBytes!!.size) {
            length = size
        } else {
            length = (mMediaBytes!!.size - position).toInt()
            if (length > buffer.size)
                length = buffer.size

            length--
        }
        System.arraycopy(mMediaBytes!!, position.toInt(), buffer, offset, length)

        return length
    }

    @Throws(IOException::class)
    override fun getSize(): Long {
        val length = mDescriptor!!.length
        if (mMediaBytes == null) {
            val inputStream = mDescriptor!!.createInputStream()
            mMediaBytes = readBytes(inputStream)
        }


        return length
    }

    @Throws(IOException::class)
    override fun close() {
        if (mDescriptor != null)
            mDescriptor!!.close()

        mDescriptor = null
        mMediaBytes = null
    }

    @Throws(IOException::class)
    private fun readBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()

        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len = inputStream.read(buffer)
        while (len != -1) {
            byteBuffer.write(buffer, 0, len)
            len = inputStream.read(buffer)
        }

        return byteBuffer.toByteArray()
    }

    companion object {

        fun create(context: Context, uri: Uri): RawDataSourceProvider? {
            try {
                val fileDescriptor = context.contentResolver.openAssetFileDescriptor(uri, "r")
                return RawDataSourceProvider(fileDescriptor)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return null
        }
    }
}