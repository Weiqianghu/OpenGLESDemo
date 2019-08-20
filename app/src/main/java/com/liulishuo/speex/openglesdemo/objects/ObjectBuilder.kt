package com.liulishuo.speex.openglesdemo.objects

import android.opengl.GLES20
import com.liulishuo.speex.openglesdemo.util.Geometry
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val FLOATS_PER_VERTEX = 3

class ObjectBuilder(sizeInVertices: Int) {
    private var offset = 0
    private val vertexData: FloatArray = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)
    private val drawList = mutableListOf<DrawCommand>()

    companion object {
        private fun sizeOfCircleInVertices(numPoints: Int): Int {
            return 1 + numPoints + 1
        }

        private fun sizeOfOpenCylinderInVertices(numPoints: Int): Int {
            return (numPoints + 1) * 2
        }

        fun createPuck(puck: Geometry.Cylinder, numPoints: Int): GenerateData {
            val size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints)
            val builder = ObjectBuilder(size)
            val puckTop = Geometry.Circle(puck.center.translateY(puck.height / 2f), puck.radius)

            builder.appendCircle(puckTop, numPoints)
            builder.appendOpenCylinder(puck, numPoints)
            return builder.build()
        }

        fun createMallet(center: Geometry.Point, radius: Float, height: Float, numPoints: Int): GenerateData {
            val size = sizeOfCircleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2

            val builder = ObjectBuilder(size)
            val baseHeight = height * 0.25f
            val baseCircle = Geometry.Circle(center.translateY(-baseHeight), radius)
            val baseCylinder = Geometry.Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight)

            builder.appendCircle(baseCircle, numPoints)
            builder.appendOpenCylinder(baseCylinder, numPoints)

            val handleHeight = height * 0.75f
            val handleRadius = radius / 3f
            val handleCircle = Geometry.Circle(center.translateY(height * 0.5f), handleRadius)
            val handleCylinder =
                Geometry.Cylinder(handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight)

            builder.appendCircle(handleCircle, numPoints)
            builder.appendOpenCylinder(handleCylinder, numPoints)

            return builder.build()
        }
    }

    fun appendCircle(circle: Geometry.Circle, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfCircleInVertices(numPoints)

        vertexData[offset++] = circle.center.x
        vertexData[offset++] = circle.center.y
        vertexData[offset++] = circle.center.z

        for (i in 0..numPoints) {
            val angleInRadians = (i.toFloat() / numPoints.toFloat()) * PI * 2f
            vertexData[offset++] = circle.center.x + circle.radius * cos(angleInRadians).toFloat()
            vertexData[offset++] = circle.center.y
            vertexData[offset++] = circle.center.z + circle.radius * sin(angleInRadians).toFloat()
        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices)
            }
        })
    }

    fun appendOpenCylinder(cylinder: Geometry.Cylinder, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfOpenCylinderInVertices(numPoints)

        val yStart = cylinder.center.y - (cylinder.height / 2f)
        val yEnd = cylinder.center.y + (cylinder.height / 2f)

        for (i in 0..numPoints) {
            val angleInRadians = (i.toFloat() / numPoints.toFloat()) * PI * 2f
            val xPosition = cylinder.center.x + cylinder.radius * cos(angleInRadians).toFloat()
            val zPosition = cylinder.center.z + cylinder.radius * sin(angleInRadians).toFloat()

            vertexData[offset++] = xPosition
            vertexData[offset++] = yStart
            vertexData[offset++] = zPosition

            vertexData[offset++] = xPosition
            vertexData[offset++] = yEnd
            vertexData[offset++] = zPosition
        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices)
            }
        })
    }

    fun build(): GenerateData {
        return GenerateData(vertexData, drawList)
    }

    data class GenerateData(val vertexData: FloatArray, val drawList: List<DrawCommand>) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GenerateData

            if (!vertexData.contentEquals(other.vertexData)) return false
            if (drawList != other.drawList) return false

            return true
        }

        override fun hashCode(): Int {
            var result = vertexData.contentHashCode()
            result = 31 * result + drawList.hashCode()
            return result
        }
    }

    interface DrawCommand {
        fun draw()
    }
}