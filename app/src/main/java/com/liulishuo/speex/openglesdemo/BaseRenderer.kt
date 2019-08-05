package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import javax.microedition.khronos.opengles.GL10

private const val BYTES_PER_FLOAT = 4

abstract class BaseRenderer(val context: Context) : GLSurfaceView.Renderer {
    protected var program: Int = 0
    var isReadCurrentFrame = false
    protected var outputWidth: Int = 0
    protected var outputHeight: Int = 0
    var rendererCallback: RendererCallback? = null

    interface RendererCallback {
        /**
         * 渲染完毕
         *
         * @param data   缓存数据
         * @param width  数据宽度
         * @param height 数据高度
         */
        fun onRendererDone(data: ByteBuffer, width: Int, height: Int)
    }

    protected fun makeProgram(vertexShader: String, fragmentShader: String) {
        program = GLUtil.makeProgram(vertexShader, fragmentShader)
        GLES20.glUseProgram(program)
    }

    protected fun getUniform(name: String): Int {
        return GLES20.glGetUniformLocation(program, name)
    }

    protected fun getAttrib(name: String): Int {
        return GLES20.glGetAttribLocation(program, name)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        outputWidth = width
        outputHeight = height
    }

    protected fun onReadPixel(x: Int = 0, y: Int = 0, width: Int = outputWidth, height: Int = outputHeight) {
        if (!isReadCurrentFrame) {
            return
        }
        isReadCurrentFrame = false
        val buffer = ByteBuffer.allocate(width * height * BYTES_PER_FLOAT)
        GLES20.glReadPixels(x, y, width, height, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, buffer)
        rendererCallback?.onRendererDone(buffer, width, height)
    }

    protected fun readPixel(w: Int = outputWidth, h: Int = outputHeight): Bitmap {
        val buffer = ByteBuffer.allocate(w * h * BYTES_PER_FLOAT)
        GLES20.glReadPixels(
            0, 0, w, h, GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE, buffer
        )
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }

    open fun onClick() {}
}