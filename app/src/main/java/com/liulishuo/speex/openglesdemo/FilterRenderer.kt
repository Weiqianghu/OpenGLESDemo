package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.opengl.GLES20
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs
import kotlin.math.sin

class FilterRenderer(context: Context) : BaseRenderer(context) {
    private val filterList = ArrayList<BaseFilter>()
    private var drawIndex = 0
    private var isChanged = false
    private var currentFilter: BaseFilter
    private var texture: Texture? = null

    init {
        filterList.add(BaseFilter(context))
        filterList.add(GrayFilter(context))
        filterList.add(InverseFilter(context))
        filterList.add(LightUpFilter(context))

        currentFilter = filterList[0]
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        currentFilter.onCreated()
        texture = TextureHelper.loadTexture(context, R.drawable.pikachu)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        currentFilter.onSizeChanged(width, height)
        currentFilter.texture = texture
    }

    override fun onDrawFrame(gl: GL10?) {
        if (isChanged) {
            currentFilter = filterList[drawIndex]
            filterList
                .filter { it != currentFilter }
                .forEach {
                    it.onDestroy()
                }

            currentFilter.onCreated()
            currentFilter.onSizeChanged(outputWidth, outputHeight)
            currentFilter.texture = texture
            isChanged = false
        }

        currentFilter.onDraw()
    }

    override fun onClick() {
        super.onClick()
        drawIndex++
        drawIndex = if (drawIndex >= filterList.size) 0 else drawIndex
        isChanged = true
    }
}

class InverseFilter(context: Context) : BaseFilter(context, BaseFilter.VERTEX_SHADER, INVERSE_FRAGMENT_SHADER) {
    companion object {
        private const val INVERSE_FRAGMENT_SHADER = """
                precision mediump float;
                varying vec2 v_TexCoord;
                uniform sampler2D u_TextureUnit;
                void main() {
                    vec4 src = texture2D(u_TextureUnit, v_TexCoord);
                    gl_FragColor = vec4(1.0 - src.r, 1.0 - src.g, 1.0 - src.b, 1.0);
                }
                """
    }
}

class GrayFilter(context: Context) : BaseFilter(context, VERTEX_SHADER, GRAY_FRAGMENT_SHADER) {
    companion object {
        private const val GRAY_FRAGMENT_SHADER = """
                precision mediump float;
                varying vec2 v_TexCoord;
                uniform sampler2D u_TextureUnit;
                void main() {
                    vec4 src = texture2D(u_TextureUnit, v_TexCoord);
                    float gray = (src.r + src.g + src.b) / 3.0;
                    gl_FragColor =vec4(gray, gray, gray, 1.0);
                }
                """
    }
}

class LightUpFilter(context: Context) : BaseFilter(context, VERTEX_SHADER, LIGHT_FRAGMENT_SHADER) {
    companion object {
        private const val LIGHT_FRAGMENT_SHADER = """
            precision mediump float;
            varying vec2 v_TexCoord;
            uniform sampler2D u_TextureUnit;
            uniform float intensity;
            void main(){
                vec4 src = texture2D(u_TextureUnit, v_TexCoord);
                vec4 addColor = vec4(intensity, intensity, intensity, 1.0);
                gl_FragColor = src + addColor;
            }
        """
    }

    private var intensityLocation = 0
    private var startTime: Long = 0

    override fun onCreated() {
        super.onCreated()
        startTime = System.currentTimeMillis()
        intensityLocation = GLES20.glGetUniformLocation(program, "intensity")
    }

    override fun onDraw() {
        super.onDraw()
        val intensity = abs(sin((System.currentTimeMillis() - startTime) / 1000.0)) / 4.0
        GLES20.glUniform1f(intensityLocation, intensity.toFloat())
    }
}