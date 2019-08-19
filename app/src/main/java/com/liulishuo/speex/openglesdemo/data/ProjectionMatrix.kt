package com.liulishuo.speex.openglesdemo.data

import android.opengl.Matrix

class ProjectionMatrix {
    val projectionMatrix = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    fun onSurfaceChanged(width: Int, height: Int) {
        val aspectRatio = if (width > height) {
            width.toFloat() / height.toFloat()
        } else {
            height.toFloat() / width.toFloat()
        }

        if (width > height) {
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 0f, 10f)
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, 0f, 10f)
        }
    }
}