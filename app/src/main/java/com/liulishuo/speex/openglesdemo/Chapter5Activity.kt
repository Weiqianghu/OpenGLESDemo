package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Chapter5Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LocalGLSurfaceView5(this))
    }
}

private class LocalGLSurfaceView5 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {
    init {
        setEGLContextClientVersion(2)
        setRenderer(LocalRender5())
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}

private class LocalRender5 : GLSurfaceView.Renderer {
    private val pointData = floatArrayOf(
        -0.5f, -0.5f,
        0.5f, -0.5f,
        -0.5f, 0.5f,
        0.5f, 0.5f
    )
    private val colorData = floatArrayOf(
        1f, 0.5f, 0.5f,
        1f, 0f, 1f,
        0f, 1f, 1f,
        1f, 1f, 0f
    )

    private val positionComponentCount = 2
    private val colorComponentCount = 3

    private lateinit var projectionMatrixHelper: ProjectionMatrixHelper

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, pointData.size / positionComponentCount)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        projectionMatrixHelper.onSurfaceChanged(width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)

        val program = GLUtil.makeProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        val colorHandle = GLES20.glGetAttribLocation(program, "a_Color")
        projectionMatrixHelper = ProjectionMatrixHelper(program, "u_Matrix")

        val vertexData = GLUtil.createFloatBuffer(pointData)
        vertexData.position(0)
        GLES20.glVertexAttribPointer(positionHandle, positionComponentCount, GLES20.GL_FLOAT, false, 0, vertexData)
        GLES20.glEnableVertexAttribArray(positionHandle)

        val colorBuffer = GLUtil.createFloatBuffer(colorData)
        colorBuffer.position(0)
        GLES20.glVertexAttribPointer(colorHandle, colorComponentCount, GLES20.GL_FLOAT, false, 0, colorBuffer)
        GLES20.glEnableVertexAttribArray(colorHandle)
    }
}

private const val VERTEX_SHADER = """
    uniform mat4 u_Matrix;
    attribute vec4 a_Position;
    attribute vec4 a_Color;
    varying vec4 v_Color;
    void main(){
        v_Color = a_Color;
        gl_Position = u_Matrix * a_Position;
    }
"""

private const val FRAGMENT_SHADER = """
    precision mediump float;
     varying vec4 v_Color;
     void main(){
        gl_FragColor = v_Color;
     }
"""