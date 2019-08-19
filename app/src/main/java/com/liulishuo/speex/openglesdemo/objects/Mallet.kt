package com.liulishuo.speex.openglesdemo.objects

import android.opengl.GLES20
import com.liulishuo.speex.openglesdemo.data.BYTES_PER_FLOAT
import com.liulishuo.speex.openglesdemo.data.VertexArray
import com.liulishuo.speex.openglesdemo.programs.ColorShaderProgram

private const val POSITION_COMPONENT_COUNT = 2
private const val COLOR_COMPONENT_COUNT = 3
private const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

class Mallet {
    private val VERTEX_DATA = floatArrayOf(
        0f, -0.4f, 1f, 1f, 1f,
        0f, 0.4f, 1f, 1f, 1f
    )

    private val vertexArray = VertexArray(VERTEX_DATA)

    fun bindData(colorShaderProgram: ColorShaderProgram) {
        vertexArray.setVertexAttributePointer(
            0,
            colorShaderProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )

        vertexArray.setVertexAttributePointer(
            0,
            colorShaderProgram.getColorAttributeLocation(),
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2)
    }
}