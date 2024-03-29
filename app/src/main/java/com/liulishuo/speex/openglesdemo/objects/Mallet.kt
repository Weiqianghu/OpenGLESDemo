package com.liulishuo.speex.openglesdemo.objects

import com.liulishuo.speex.openglesdemo.data.BYTES_PER_FLOAT
import com.liulishuo.speex.openglesdemo.data.VertexArray
import com.liulishuo.speex.openglesdemo.programs.ColorShaderProgram
import com.liulishuo.speex.openglesdemo.util.Geometry

private const val POSITION_COMPONENT_COUNT = 3
private const val COLOR_COMPONENT_COUNT = 3
private const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

class Mallet(radius: Float, val height: Float, numPointsAroundMallet: Int) {
    private val vertexArray: VertexArray
    private val drawList: List<ObjectBuilder.DrawCommand>

    init {
        val malletData = ObjectBuilder.createMallet(
            Geometry.Point(0f, 0f, 0f), radius, height, numPointsAroundMallet
        )
        vertexArray = VertexArray(malletData.vertexData)
        drawList = malletData.drawList
    }

    fun bindData(colorShaderProgram: ColorShaderProgram) {
        vertexArray.setVertexAttributePointer(
            0,
            colorShaderProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            0
        )
    }

    fun draw() {
        drawList.forEach {
            it.draw()
        }
    }
}