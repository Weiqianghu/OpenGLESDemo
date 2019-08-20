package com.liulishuo.speex.openglesdemo.objects

import com.liulishuo.speex.openglesdemo.data.VertexArray
import com.liulishuo.speex.openglesdemo.programs.ColorShaderProgram
import com.liulishuo.speex.openglesdemo.util.Geometry

private const val POSITION_COMPONENT_COUNT = 3

class Puck(radius: Float, val height: Float, numPointsAroundPuck: Int) {
    private val vertexArray: VertexArray
    private val drawList: List<ObjectBuilder.DrawCommand>

    init {
        val puckData = ObjectBuilder.createPuck(
            Geometry.Cylinder(Geometry.Point(0f, 0f, 0f), radius, height),
            numPointsAroundPuck
        )
        vertexArray = VertexArray(puckData.vertexData)
        drawList = puckData.drawList
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