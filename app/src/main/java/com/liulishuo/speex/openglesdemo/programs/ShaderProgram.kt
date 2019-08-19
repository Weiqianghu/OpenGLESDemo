package com.liulishuo.speex.openglesdemo.programs

import android.content.Context
import android.opengl.GLES20
import android.support.annotation.RawRes
import com.liulishuo.speex.openglesdemo.GLUtil
import com.liulishuo.speex.openglesdemo.TextResourceReader

const val U_MATRIX = "u_Matrix"
const val U_TEXTURE_UNINT = "u_TextureUnit"

const val A_POSITION = "a_Position"
const val A_COLOR = "a_Color"
const val A_TEXTURE_COORDINATES = "a_TextureCoordinates"

open class ShaderProgram(context: Context, @RawRes vertexShaderResourceId: Int, @RawRes fragmentShaderResourceId: Int) {

    protected val program: Int = GLUtil.makeProgram(
        TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
        TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId)
    )

    fun useProgram() {
        GLES20.glUseProgram(program)
    }
}