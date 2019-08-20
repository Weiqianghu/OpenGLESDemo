package com.liulishuo.speex.openglesdemo.programs

import android.content.Context
import android.opengl.GLES20
import com.liulishuo.speex.openglesdemo.R

class ColorShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader) {
    private var uMatrixLocation: Int = 0

    private var aPositionLocation = 0
    private var uColorLocation = 0

    init {
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX)
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR)

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
    }

    fun setUniforms(matrix: FloatArray, r: Float, g: Float, b: Float) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glUniform4f(uColorLocation, r, g, b, 1f)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    fun getColorUniformLocation(): Int {
        return uColorLocation
    }
}