package com.liulishuo.speex.openglesdemo.objects

import android.opengl.GLES20
import com.liulishuo.speex.openglesdemo.data.BYTES_PER_FLOAT
import com.liulishuo.speex.openglesdemo.data.VertexArray
import com.liulishuo.speex.openglesdemo.programs.TextureShaderProgram

private const val POSITION_COMPONENT_COUNT = 2
private const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
private const val STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT

class Table {
    private val VERTEX_DATA = floatArrayOf(
        0f, 0f, 0.5f, 0.5f,
        -0.5f, -0.8f, 0f, 0.9f,
        0.5f, -0.8f, 1f, 0.9f,
        0.5f, 0.8f, 1f, 0.1f,
        -0.5f, 0.8f, 0f, 0.1f,
        -0.5f, -0.8f, 0f, 0.9f
    )

    private val vertexArray = VertexArray(VERTEX_DATA)

    fun bindData(textureShaderProgram: TextureShaderProgram) {
        vertexArray.setVertexAttributePointer(
            0,
            textureShaderProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        vertexArray.setVertexAttributePointer(
            POSITION_COMPONENT_COUNT,
            textureShaderProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE
        )
    }

    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
    }
}