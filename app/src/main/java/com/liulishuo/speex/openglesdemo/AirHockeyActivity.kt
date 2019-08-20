package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import com.liulishuo.speex.openglesdemo.data.PerspectiveMatrix
import com.liulishuo.speex.openglesdemo.data.ProjectionMatrix
import com.liulishuo.speex.openglesdemo.objects.Mallet
import com.liulishuo.speex.openglesdemo.objects.Puck
import com.liulishuo.speex.openglesdemo.objects.Table
import com.liulishuo.speex.openglesdemo.programs.ColorShaderProgram
import com.liulishuo.speex.openglesdemo.programs.TextureShaderProgram
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
//        renderMode = RENDERMODE_WHEN_DIRTY
    }
}

class AirHockeyRenderer(context: Context) : BaseRenderer(context) {

    private lateinit var perspectiveMatrix: PerspectiveMatrix
    private var table: Table? = null
    private var mallet: Mallet? = null
    private var puck: Puck? = null

    private lateinit var textureShaderProgram: TextureShaderProgram
    private lateinit var colorShaderProgram: ColorShaderProgram

    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private var texture: Int = 0

    private var start: Long = 0
    private val eyeXStride = 4.4 / (10 * 1000)
    private val startEyesX = -2.2
    private val endEyesX = 2.2

    private val eyeZStride = 2.2 / (5 * 1000)
    private val startEyesZ = 0
    private val endEyesZ = 2.2

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

        textureShaderProgram = TextureShaderProgram(context)
        colorShaderProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)?.textureId ?: 0

        perspectiveMatrix = PerspectiveMatrix()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        GLES20.glViewport(0, 0, width, height)
        perspectiveMatrix.onSurfaceChanged(width, height)

    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        moveEyes()
        Matrix.multiplyMM(
            viewProjectionMatrix, 0, perspectiveMatrix.perspectiveMatrix,
            0, viewMatrix, 0
        )

        positionTableInScene()
        textureShaderProgram.useProgram()
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, texture)
        table?.bindData(textureShaderProgram)
        table?.draw()

        positionObjectInScene(0f, (mallet?.height ?: 0f) / 2f, -0.4f)
        colorShaderProgram.useProgram()
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mallet?.bindData(colorShaderProgram)
        mallet?.draw()

        positionObjectInScene(0f, (mallet?.height ?: 0f) / 2f, 0.4f)
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet?.draw()

        positionObjectInScene(0f, (puck?.height ?: 0f) / 2f, 0f)
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck?.bindData(colorShaderProgram)
        puck?.draw()
    }

    private fun positionTableInScene() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    private fun moveEyes() {
        if (start == 0L) {
            start = System.currentTimeMillis()
        }
        val time = (System.currentTimeMillis() - start) % (20 * 1000)
        val eyesX = if (time <= 10 * 1000) {
            startEyesX + time * eyeXStride
        } else {
            endEyesX - (time - 10 * 1000) * eyeXStride
        }
        val eyesZ = when {
            time <= 5 * 1000 -> startEyesZ + time * eyeZStride
            time <= 10 * 1000 -> endEyesZ - (time - 5 * 1000) * eyeZStride
            time <= 15 * 1000 -> startEyesZ - (time - 10 * 1000) * eyeZStride
            else -> -endEyesZ + (time - 15 * 1000) * eyeZStride
        }

        Matrix.setLookAtM(
            viewMatrix, 0, eyesX.toFloat(), 1.2f, eyesZ.toFloat(), 0f,
            0f, 0f, 0f, 1f, 0f
        )
    }
}
