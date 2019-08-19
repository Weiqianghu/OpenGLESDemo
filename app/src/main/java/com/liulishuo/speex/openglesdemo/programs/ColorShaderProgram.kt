package com.liulishuo.speex.openglesdemo.programs

import android.content.Context
import android.opengl.GLES20
import com.liulishuo.speex.openglesdemo.R

class ColorShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader) {
    private var uMatrixLocation: Int = 0

    private var aPositionLocation = 0
    private var aColorPositionLocation = 0

    init {
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX)

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
        aColorPositionLocation = GLES20.glGetAttribLocation(program, A_COLOR)
    }

    fun setUniforms(matrix: FloatArray) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    fun getColorAttributeLocation(): Int {
        return aColorPositionLocation
    }
}