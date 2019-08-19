package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import com.liulishuo.speex.openglesdemo.data.PerspectiveMatrix
import com.liulishuo.speex.openglesdemo.data.ProjectionMatrix
import com.liulishuo.speex.openglesdemo.objects.Mallet
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
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}

class AirHockeyRenderer(context: Context) : BaseRenderer(context) {

    private lateinit var perspectiveMatrix: PerspectiveMatrix
    private var table: Table? = null
    private var mallet: Mallet? = null

    private lateinit var textureShaderProgram: TextureShaderProgram
    private lateinit var colorShaderProgram: ColorShaderProgram

    private var texture: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        table = Table()
        mallet = Mallet()

        textureShaderProgram = TextureShaderProgram(context)
        colorShaderProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)?.textureId ?: 0

        perspectiveMatrix = PerspectiveMatrix()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        perspectiveMatrix.onSurfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        textureShaderProgram.useProgram()
        textureShaderProgram.setUniforms(perspectiveMatrix.perspectiveMatrix, texture)
        table?.bindData(textureShaderProgram)
        table?.draw()

        colorShaderProgram.useProgram()
        colorShaderProgram.setUniforms(perspectiveMatrix.perspectiveMatrix)
        mallet?.bindData(colorShaderProgram)
        mallet?.draw()
    }
}


