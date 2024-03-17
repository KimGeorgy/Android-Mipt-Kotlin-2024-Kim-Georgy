fun main() {
    val (ax, ay, bx, by) = readln().split(' ').map(String::toDouble)
    val ab = Segment(Point(ax, ay), Point(bx, by))
    val n = readln().toInt()
    val intersectionPoints: MutableSet<Point> = mutableSetOf()
    for (i in 1..n) {
        val (cx, cy, dx, dy) = readln().split(' ').map(String::toDouble)
        val cd = Segment(Point(cx, cy), Point(dx, dy))
        val intPoint = ab.intersect(cd)
        if (intPoint != null) {
            intersectionPoints.add(intPoint)
        }
    }

    println(intersectionPoints.size)
}

data class Point(var x: Double, var y: Double)

class Segment(var a: Point, var b: Point) {
    private fun det (a: Double, b: Double, c: Double, d: Double): Double {
        return a * d - b * c
    }

    fun intersect(seg: Segment): Point? {
        val a1 = a.y - b.y
        val b1 = b.x - a.x
        val c1 = -a1 * a.x - b1 * a.y
        val a2 = seg.a.y - seg.b.y
        val b2 = seg.b.x - seg.a.x
        val c2 = -a2 * seg.a.x - b2 * seg.a.y
        val zn = det(a1, b1, a2, b2)
        return if (zn != .0) {
            val x = -det(c1, b1, c2, b2) / zn
            val y = -det(a1, c1, a2, c2) / zn
            if (x <= maxOf(a.x, b.x) && x <= maxOf(seg.a.x, seg.b.x)
                && x >= minOf(a.x, b.x) && x >= minOf(seg.a.x, seg.b.x)
                && y <= maxOf(a.y, b.y) && y <= maxOf(seg.a.y, seg.b.y)
                && y >= minOf(a.y, b.y) && y >= minOf(seg.a.y, seg.b.y))
                Point(x, y)
            else null
        } else null
    }
}
