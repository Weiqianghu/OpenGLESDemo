package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(AirHockeyGLSurfaceView(this))
    }
}

private class AirHockeyGLSurfaceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {
    init {
        setEGLContextClientVersion(2)
        val airHockeyRenderer = AirHockeyRenderer(context)
        setRenderer(airHockeyRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}

private const val POSITION_COMPONENT_COUNT = 2
private const val COLOR_COMPONENT_COUNT = 3
private const val BYTES_PER_FLOAT = 4
private const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

private

class AirHockeyRenderer(context: Context) : BaseRenderer(context) {

    private val tableVertices = floatArrayOf(
        0f, 0f, 1f, 1f, 1f,
        -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
        0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
        0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
        -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
        -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

        -0.5f, 0f, 1f, 0f, 0f,
        0.5f, 0f, 1f, 0f, 0f,

        0f, -0.4f, 0f, 0f, 1f,
        0f, 0.4f, 1f, 0f, 0f
    )

    private var aColorLocation = 0
    private var aPositionLocation = 0
    private var perspectiveMatrixHelper: PerspectiveMatrixHelper? = null

    private val vertexData = ByteBuffer.allocateDirect(tableVertices.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        vertexData.put(tableVertices)

        makeProgram(
            TextResourceReader.readTextFileFromeResource(context, R.raw.simple_vertex_shader),
            TextResourceReader.readTextFileFromeResource(context, R.raw.simple_fragment_shader)
        )

        aColorLocation = getAttrib("a_Color")
        aPositionLocation = getAttrib("a_Position")

        vertexData.position(0)
        GLES20.glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        GLES20.glEnableVertexAttribArray(aPositionLocation)

        vertexData.position(POSITION_COMPONENT_COUNT)
        GLES20.glVertexAttribPointer(
            aColorLocation,
            COLOR_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        GLES20.glEnableVertexAttribArray(aColorLocation)

        perspectiveMatrixHelper = PerspectiveMatrixHelper(program, "u_Matrix")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        perspectiveMatrixHelper?.onSurfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)

        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)

        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)

        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)
    }
}


