package com.liulishuo.speex.openglesdemo.util

import kotlin.math.sqrt

object Geometry {
    data class Point(val x: Float, val y: Float, val z: Float) {
        fun translateY(distance: Float): Point {
            return Point(x, y + distance, z)
        }

        fun translate(vector: Vector): Point {
            return Point(x + vector.x, y + vector.y, z + vector.z)
        }
    }

    data class Circle(val center: Point, val radius: Float) {
        fun scale(scale: Float): Circle {
            return Circle(center, radius * scale)
        }
    }

    data class Cylinder(val center: Point, val radius: Float, val height: Float)

    data class Vector(val x: Float, val y: Float, val z: Float) {
        fun length(): Float {
            return sqrt(x * x + y * y + z * z)
        }

        fun crossProduct(other: Vector): Vector {
            return Vector(
                (y * other.z) - (z * other.y),
                (z * other.x) - (x * other.z),
                (x * other.y) - (y * other.x)
            )
        }
    }

    data class Ray(val point: Point, val vector: Vector)

    data class Sphere(val center: Point, val radius: Float)

    fun vectorBetween(from: Point, to: Point): Vector {
        return Vector(
            to.x - from.x,
            to.y - from.y,
            to.z - from.z
        )
    }

    fun intersects(sphere: Sphere, ray: Ray): Boolean {
        return distanceBetween(sphere.center, ray) < sphere.radius
    }

    private fun distanceBetween(point: Point, ray: Ray): Float {
        val p1ToPoint = vectorBetween(ray.point, point)
        val p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point)

        val areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length()
        val lengthOfBase = ray.vector.length()

        return areaOfTriangleTimesTwo / lengthOfBase
    }
}