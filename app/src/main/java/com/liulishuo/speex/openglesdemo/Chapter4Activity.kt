package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class Chapter4Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LocalGLSurfaceView4(this))
    }
}

private class LocalGLSurfaceView4 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {
    init {
        setEGLContextClientVersion(2)
        setRenderer(LocalRenderer4())
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}

private const val RADIUS = 0.5f

private class LocalRenderer4 : GLSurfaceView.Renderer {
    private var program: Int = 0
    private var vertexData = FloatBuffer.allocate(0)

    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private var uMatrixHandle: Int = 0

    private var polygonVertexCount = 4
    private var pointData = FloatArray(0)
    private var projectionMatrix = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        updateVertexData()
        draw()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val aspectRatio = if (width > height) {
            width.toFloat() / height.toFloat()
        } else {
            height.toFloat() / width.toFloat()
        }

        if (width > height) {
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }
        GLES20.glUniformMatrix4fv(uMatrixHandle, 1, false, projectionMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
        program = GLUtil.makeProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
        colorHandle = GLES20.glGetUniformLocation(program, "u_Color")
        uMatrixHandle = GLES20.glGetUniformLocation(program, "u_Matrix")
    }

    private fun draw() {
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexData
        )
        GLES20.glUniform4f(colorHandle, 0.2f, 0.6f, 0f, 1f)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, polygonVertexCount + 2)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun updateVertexData() {
        pointData = FloatArray((polygonVertexCount + 2) * 2)
        val radian = 2 * Math.PI / polygonVertexCount
        pointData[0] = 0f
        pointData[1] = 0f
        for (i in 0 until polygonVertexCount) {
            pointData[2 * i + 2] = (RADIUS * cos(radian * i)).toFloat()
            pointData[2 * i + 2 + 1] = (RADIUS * sin(radian * i)).toFloat()
        }

        pointData[2 * polygonVertexCount + 2] = (RADIUS * cos(0.0)).toFloat()
        pointData[2 * polygonVertexCount + 2 + 1] = (RADIUS * sin(0.0)).toFloat()

        vertexData = GLUtil.createFloatBuffer(pointData)
        vertexData.position(0)
    }
}

private const val VERTEX_SHADER = """
                // vec4：4个分量的向量：x、y、z、w
                attribute vec4 a_Position;
                uniform mat4 u_Matrix;
                void main()
                {
                // gl_Position：GL中默认定义的输出变量，决定了当前顶点的最终位置
                    gl_Position =  u_Matrix * a_Position;
                // gl_PointSize：GL中默认定义的输出变量，决定了当前顶点的大小
                   // gl_PointSize = 240.0;
                }
        """

private const val FRAGMENT_SHADER = """
                // 定义所有浮点数据类型的默认精度；有lowp、mediump、highp 三种，但只有部分硬件支持片段着色器使用highp。(顶点着色器默认highp)
                precision mediump float;
                uniform vec4 u_Color;
                void main()
                {
                // gl_FragColor：GL中默认定义的输出变量，决定了当前片段的最终颜色
                   gl_FragColor = u_Color;
                }
        """

private const val POSITION_COMPONENT_COUNT = 2
