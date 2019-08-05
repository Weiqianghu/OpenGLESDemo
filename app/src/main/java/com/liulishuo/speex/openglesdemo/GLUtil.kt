package com.liulishuo.speex.openglesdemo

import android.opengl.GLES20
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

private const val TAG = "GLUtil"
private const val BYTES_PER_FLOAT = 4
private const val BYTES_PER_SHORT = 2

object GLUtil {
    fun makeProgram(vertexShader: String, fragmentShader: String): Int {
        val vertexShaderId = compileVertexShader(vertexShader)
        val fragmentShaderId = compileFragmentShader(fragmentShader)

        val program = linkProgram(vertexShaderId, fragmentShaderId)
        if (validateProgram(program)) {
            return program
        }
        return 0
    }

    private fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programId = GLES20.glCreateProgram()
        if (programId == 0) {
            Log.e(TAG, "could not create new program.")
            return 0
        }

        GLES20.glAttachShader(programId, vertexShaderId)
        GLES20.glAttachShader(programId, fragmentShaderId)

        GLES20.glLinkProgram(programId)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        Log.v(TAG, "Results of linking program:\n" + GLES20.glGetProgramInfoLog(programId))

        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(programId)
            return 0
        }

        return programId
    }

    private fun validateProgram(programObjectId: Int): Boolean {
        GLES20.glValidateProgram(programObjectId)
        val validateStatus = IntArray(1)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)
        Log.v(
            TAG, "Results of validating program: " + validateStatus[0]
                    + "\nLog:" + GLES20.glGetProgramInfoLog(programObjectId)
        )

        return validateStatus[0] != 0
    }

    fun createFloatBuffer(array: FloatArray): FloatBuffer {
        return ByteBuffer.allocateDirect(array.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(array)
                position(0)
            }
    }

    fun createShortBuffer(array: ShortArray): ShortBuffer {
        return ByteBuffer.allocateDirect(array.size * BYTES_PER_SHORT)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply {
                put(array)
                position(0)
            }
    }

    private fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)
    }

    private fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderObjectId = GLES20.glCreateShader(type)
        if (shaderObjectId == 0) {
            Log.e(TAG, "could not create new shader.")
            return 0
        }
        GLES20.glShaderSource(shaderObjectId, shaderCode)
        GLES20.glCompileShader(shaderObjectId)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        Log.d(
            TAG,
            "Results of compiling source:" + "\n" + shaderCode + "\n:" + GLES20.glGetShaderInfoLog(shaderObjectId)
        )

        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderObjectId)
            return 0
        }

        return shaderObjectId
    }
}