package com.liulishuo.speex.openglesdemo

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.support.annotation.DrawableRes
import android.util.Log

private const val TAG = "TextureHelper"

object TextureHelper {
    fun loadTexture(context: Context, @DrawableRes drawableId: Int): Texture? {
        val textureObjectIds = IntArray(1)
        GLES20.glGenTextures(1, textureObjectIds, 0)
        if (textureObjectIds[0] == 0) {
            Log.w(TAG, "Could not generate a new OpenGL texture object.")
            return null
        }

        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId, options)
        if (bitmap == null) {
            Log.w(TAG, "Resource ID : $drawableId  could not be decoded.")
            GLES20.glDeleteTextures(1, textureObjectIds, 0)
            return null
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        val texture = Texture(textureObjectIds[0], bitmap.width, bitmap.height)
        bitmap.recycle()
        return texture
    }
}

data class Texture(val textureId: Int, val width: Int, val height: Int)