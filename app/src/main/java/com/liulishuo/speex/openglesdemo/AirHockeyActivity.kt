package com.liulishuo.speex.openglesdemo

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.liulishuo.speex.openglesdemo.data.PerspectiveMatrix
import com.liulishuo.speex.openglesdemo.objects.Mallet
import com.liulishuo.speex.openglesdemo.objects.Puck
import com.liulishuo.speex.openglesdemo.objects.Table
import com.liulishuo.speex.openglesdemo.programs.ColorShaderProgram
import com.liulishuo.speex.openglesdemo.programs.TextureShaderProgram
import com.liulishuo.speex.openglesdemo.util.Geometry
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "AirHockeyActivity"

class AirHockeyActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val airHockeyGLSurfaceView = AirHockeyGLSurfaceView(this)
        val airHockeyRenderer = AirHockeyRenderer(this)
        airHockeyGLSurfaceView.setRenderer(airHockeyRenderer)

        airHockeyGLSurfaceView.setOnTouchListener { v, event ->
            val normalizedX = (event.x / v.width.toFloat()) * 2 - 1
            val normalizedY = -((event.y / v.height.toFloat()) * 2 - 1)

            if (event.action == MotionEvent.ACTION_DOWN) {
                airHockeyGLSurfaceView.queueEvent {
                    airHockeyRenderer.handleTouchPress(normalizedX, normalizedY)
                }
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                airHockeyGLSurfaceView.queueEvent {
                    airHockeyRenderer.handleTouchDrag(normalizedX, normalizedY)
                }
            }
            return@setOnTouchListener true
        }
        setContentView(airHockeyGLSurfaceView)
    }
}

private class AirHockeyGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) :
    GLSurfaceView(context, attrs) {
    init {
        setEGLContextClientVersion(2)
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

    private val invertedViewProjectionMatrix = FloatArray(16)

    private var texture: Int = 0

    private var start: Long = 0
    private val eyeXStride = 4.4 / (10 * 1000)
    private val startEyesX = -2.2
    private val endEyesX = 2.2

    private val eyeZStride = 2.2 / (5 * 1000)
    private val startEyesZ = 0
    private val endEyesZ = 2.2

    private var malletPressed = false
    private lateinit var blueMalletPosition: Geometry.Point

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
        Matrix.setLookAtM(
            viewMatrix, 0, 0f, 1.2f, 2.2f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        moveEyes()
        Matrix.multiplyMM(
            viewProjectionMatrix, 0, perspectiveMatrix.perspectiveMatrix,
            0, viewMatrix, 0
        )
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0)

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

        blueMalletPosition = Geometry.Point(0f, (mallet?.height ?: 0f) / 2f, 0.4f)

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

    fun handleTouchPress(normalizedX: Float, normalizedY: Float) {
        val ray = convertNormalized2DPointToRay(normalizedX, normalizedY)

        val malletBoundingSphere = Geometry.Sphere(
            Geometry.Point(
                blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z
            ), (mallet?.height ?: 0f) / 2
        )

        malletPressed = Geometry.intersects(malletBoundingSphere, ray)
        Log.d(TAG, "malletPressed is $malletPressed")
    }

    fun handleTouchDrag(x: Float, y: Float) {

    }

    private fun convertNormalized2DPointToRay(
        normalizedX: Float,
        normalizedY: Float
    ): Geometry.Ray {
        val nearPointNdc = floatArrayOf(normalizedX, normalizedY, -1f, 1f)
        val farPointNdc = floatArrayOf(normalizedX, normalizedY, 1f, 1f)

        val nearPointWorld = FloatArray(4)
        val farPointWorld = FloatArray(4)

        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0)
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0)

        divideByW(nearPointWorld)
        divideByW(farPointWorld)

        val nearPointRay = Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2])
        val farPointRay = Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2])

        return Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay))
    }

    private fun divideByW(vector: FloatArray) {
        vector[0] /= vector[3]
        vector[1] /= vector[3]
        vector[2] /= vector[3]
    }
}
