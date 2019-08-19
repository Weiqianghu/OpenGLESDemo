package com.liulishuo.speex.openglesdemo.programs

import android.content.Context
import android.opengl.GLES20
import com.liulishuo.speex.openglesdemo.R

class TextureShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader) {
    private var uMatrixLocation: Int = 0
    private var uTextureUnitLocation = 0

    private var aPositionLocation = 0
    private var aTextureCoordinatesLocation = 0

    init {
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX)
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNINT)

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
        aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES)
    }

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    fun getTextureCoordinatesAttributeLocation(): Int {
        return aTextureCoordinatesLocation
    }
}