package com.liulishuo.speex.openglesdemo

import android.opengl.GLES20
import android.opengl.Matrix

class ProjectionMatrixHelper(programHandle: Int, matrixName: String) {
    private val matrixHandle: Int = GLES20.glGetUniformLocation(programHandle, matrixName)
    private var projectionMatrix = floatArrayOf(
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
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, projectionMatrix, 0)
    }
}