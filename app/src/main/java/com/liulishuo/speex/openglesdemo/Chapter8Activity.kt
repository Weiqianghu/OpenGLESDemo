package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet

class Chapter8Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LocalGLSurfaceView8(this))
    }
}

private class LocalGLSurfaceView8 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {
    init {
        setEGLContextClientVersion(2)
        val filterRenderer = FilterRenderer(context)
        setRenderer(filterRenderer)
//        renderMode = RENDERMODE_WHEN_DIRTY

        setOnClickListener {
            filterRenderer.onClick()
//            requestRender()
        }
    }
}