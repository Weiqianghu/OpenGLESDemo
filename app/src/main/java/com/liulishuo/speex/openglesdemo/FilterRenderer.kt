package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.opengl.GLES20
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs
import kotlin.math.cos
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
        filterList.add(TranslateFilter(context))
        filterList.add(ScaleFilter(context))

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

class TranslateFilter(context: Context) : BaseFilter(context, VERTEX_SHADER, FRAGMENT_SHADER) {
    companion object {
        private const val FRAGMENT_SHADER = """
            precision mediump float;
            varying vec2 v_TexCoord;
            uniform sampler2D u_TextureUnit;
            uniform float xV;
            uniform float yV;
            
            vec2 translate(vec2 srcCoord, float x, float y){
                return vec2(srcCoord.x + x, srcCoord.y + y);
            }
            
            void main(){
                vec2 offsetTexCoord = translate(v_TexCoord, xV, yV);
                if (offsetTexCoord.x >= 0.0 && offsetTexCoord.x <= 1.0 &&
                        offsetTexCoord.y >= 0.0 && offsetTexCoord.y <= 1.0) {
                        gl_FragColor = texture2D(u_TextureUnit, offsetTexCoord);
                }
            }
        """
    }

    private var xLocation = 0
    private var yLocation = 0
    private var startTime: Long = 0

    override fun onCreated() {
        super.onCreated()
        xLocation = GLES20.glGetUniformLocation(program, "xV")
        yLocation = GLES20.glGetUniformLocation(program, "yV")
        startTime = System.currentTimeMillis()
    }

    override fun onDraw() {
        super.onDraw()
        val intensity = sin((System.currentTimeMillis() - startTime) / 1000.0) * 0.5
        GLES20.glUniform1f(xLocation, intensity.toFloat())
        GLES20.glUniform1f(yLocation, 0.0f)
    }
}

class ScaleFilter(context: Context) : BaseFilter(context, VERTEX_SHADER, FRAGMENT_SHADER) {
    companion object {
        private const val FRAGMENT_SHADER = """
            precision mediump float;
            varying vec2 v_TexCoord;
            uniform sampler2D u_TextureUnit;
            uniform float intensity;
            
            vec2 scale(vec2 srcCoord, float x, float y){
                return vec2((srcCoord.x - 0.5)/x + 0.5, (srcCoord.y - 0.5)/y + 0.5);
            }
            
            void main(){
                vec2 offsetTexCoord = scale(v_TexCoord, intensity, intensity);
                if (offsetTexCoord.x >= 0.0 && offsetTexCoord.x <= 1.0 &&
                        offsetTexCoord.y >= 0.0 && offsetTexCoord.y <= 1.0) {
                        gl_FragColor = texture2D(u_TextureUnit, offsetTexCoord);
                }
            }
        """
    }

    private var intensityLocation = 0
    private var startTime = 0L

    override fun onCreated() {
        super.onCreated()
        intensityLocation = GLES20.glGetUniformLocation(program, "intensity")
        startTime = System.currentTimeMillis()
    }

    override fun onDraw() {
        super.onDraw()
        val intensity = abs(cos((System.currentTimeMillis() - startTime) / 1000.0) * 1)
        GLES20.glUniform1f(intensityLocation, intensity.toFloat())
    }
}