package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Chapter7Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LocalGLSurfaceView7(this))
    }
}

private class LocalGLSurfaceView7 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {
    init {
        setEGLContextClientVersion(2)
        setRenderer(LocalRender7(context))
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}

private class LocalRender7(private val context: Context) : GLSurfaceView.Renderer {
    private val vertexShader = """
        uniform mat4 u_Matrix;
        attribute vec4 a_Position;
        attribute vec2 a_TexCoord;
        varying vec2 v_TexCoord;
        void main(){
            v_TexCoord = a_TexCoord;
            gl_Position = u_Matrix * a_Position;
        }
    """

    private val fragmentShader = """
        precision mediump float;
        varying vec2 v_TexCoord;
        uniform sampler2D u_TextureUnit1;
        uniform sampler2D u_TextureUnit2;
        void main()
        {
            vec4 texture1 = texture2D(u_TextureUnit1, v_TexCoord);
            vec4 texture2 = texture2D(u_TextureUnit2, v_TexCoord);
            if (texture1.a != 0.0) {
                gl_FragColor = texture1;
            } else {
                gl_FragColor = texture2;
            }
        }
    """

    private val pointData = floatArrayOf(
        -1f, 0f,
        -1f, -1f,
        0f, -1f,
        0f, 0f
    )
    private val pointData2 = floatArrayOf(
        0f, 1f,
        0f, 0f,
        1f, 0f,
        1f, 1f
    )
    private val texVertex = floatArrayOf(
        0f, 0f,
        0f, 1f,
        1f, 1f,
        1f, 0f
    )

    private lateinit var projectionMatrixHelper: ProjectionMatrixHelper
    private var texture: Texture? = null
    private var texture2: Texture? = null

    private var uTextureUnitLocation: Int = 0
    private var uTextureUnitLocation2: Int = 0

    private lateinit var vertexData: FloatBuffer
    private lateinit var vertexData2: FloatBuffer
    private var positionHandle: Int = 0

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        texture?.let {
            vertexData.position(0)
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexData)
            GLES20.glEnableVertexAttribArray(positionHandle)

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, it.textureId)
            GLES20.glUniform1i(uTextureUnitLocation, 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, pointData.size / 2)
        }

        texture2?.let {
            vertexData2.position(0)
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexData2)
            GLES20.glEnableVertexAttribArray(positionHandle)

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, it.textureId)
            GLES20.glUniform1i(uTextureUnitLocation2, 1)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, pointData2.size / 2)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        projectionMatrixHelper.onSurfaceChanged(width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        val program = GLUtil.makeProgram(vertexShader, fragmentShader)
        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        vertexData = GLUtil.createFloatBuffer(pointData)
        vertexData2 = GLUtil.createFloatBuffer(pointData2)

        val texCoordHandle = GLES20.glGetAttribLocation(program, "a_TexCoord")
        val texVertexData = GLUtil.createFloatBuffer(texVertex)
        texVertexData.position(0)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texVertexData)
        GLES20.glEnableVertexAttribArray(texCoordHandle)

        projectionMatrixHelper = ProjectionMatrixHelper(program, "u_Matrix")

        texture = TextureHelper.loadTexture(context, R.drawable.pikachu)
        texture2 = TextureHelper.loadTexture(context, R.drawable.pikachu)

        uTextureUnitLocation = GLES20.glGetUniformLocation(program, "u_TextureUnit1")
        uTextureUnitLocation2 = GLES20.glGetUniformLocation(program, "u_TextureUnit2")

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }
}