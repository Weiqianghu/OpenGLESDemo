package com.liulishuo.speex.openglesdemo.data

import android.opengl.Matrix

class PerspectiveMatrix {
    val perspectiveMatrix = floatArrayOf(
    1f, 0f, 0f, 0f,
    0f, 1f, 0f, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f
    )
    private val modelMatrix = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    fun onSurfaceChanged(width: Int, height: Int) {
        Matrix.perspectiveM(perspectiveMatrix, 0, 60f, width.toFloat() / height.toFloat(), 1f, 10f)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -3f)
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val temp = FloatArray(16)
        Matrix.multiplyMM(temp, 0, perspectiveMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, perspectiveMatrix, 0, temp.size)
    }
}