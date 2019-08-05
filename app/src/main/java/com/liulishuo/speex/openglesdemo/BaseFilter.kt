package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.opengl.GLES20
import java.nio.FloatBuffer

open class BaseFilter(
    val context: Context,
    private val vertexShader: String = VERTEX_SHADER,
   private val fragmentShader: String = FRAGMENT_SHADER
) {
    companion object {
         const val VERTEX_SHADER = """
                uniform mat4 u_Matrix;
                attribute vec4 a_Position;
                attribute vec2 a_TexCoord;
                varying vec2 v_TexCoord;
                void main() {
                    v_TexCoord = a_TexCoord;
                    gl_Position = u_Matrix * a_Position;
                }
                """
        private const val FRAGMENT_SHADER = """
                precision mediump float;
                varying vec2 v_TexCoord;
                uniform sampler2D u_TextureUnit;
                void main() {
                    gl_FragColor = texture2D(u_TextureUnit, v_TexCoord);
                }
                """

        private const val POSITION_COMPONENT_COUNT = 2
        private val POINT_DATA = floatArrayOf(
            -1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f
        )

        private val TEX_VERTEX = floatArrayOf(
            0f, 1f,
            0f, 0f,
            1f, 0f,
            1f, 1f
        )
        private const val TEX_VERTEX_COMPONENT_COUNT = 2
    }

    private val vertexData: FloatBuffer = GLUtil.createFloatBuffer(POINT_DATA)
    private val texVertexBuffer: FloatBuffer = GLUtil.createFloatBuffer(TEX_VERTEX)
    private var textureUnitLocation = 0

    var texture: Texture? = null
    private var projectionMatrixHelper: ProjectionMatrixHelper? = null
    var program: Int = 0

    open fun onCreated() {
        makeProgram()
        val aPositionLocation = GLES20.glGetAttribLocation(program, "a_Position")
        projectionMatrixHelper = ProjectionMatrixHelper(program, "u_Matrix")

        val aTexCoordLocation = GLES20.glGetAttribLocation(program, "a_TexCoord")
        textureUnitLocation = GLES20.glGetUniformLocation(program, "u_TextureUnit")

        vertexData.position(0)
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, vertexData)
        GLES20.glEnableVertexAttribArray(aPositionLocation)

        texVertexBuffer.position(0)
        GLES20.glVertexAttribPointer(
            aTexCoordLocation,
            TEX_VERTEX_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            texVertexBuffer
        )
        GLES20.glEnableVertexAttribArray(aTexCoordLocation)

        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    open fun onSizeChanged(width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        projectionMatrixHelper?.onSurfaceChanged(width, height)
    }

    open fun onDraw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture?.textureId ?: 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, POINT_DATA.size / POSITION_COMPONENT_COUNT)
    }

    open fun onDestroy() {
        GLES20.glDeleteProgram(program)
        program = 0
    }

    private fun makeProgram() {
        program = GLUtil.makeProgram(vertexShader, fragmentShader)
        GLES20.glUseProgram(program)
    }
}