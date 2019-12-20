package ru.edu.urfu.dimon4ezzz.cargo

import org.jgrapht.GraphPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.GraphWalk
import ru.edu.urfu.dimon4ezzz.cargo.models.Point

object GraphWalkBuilder {
    /**
     * Соединяет два пути воедино, создавая из них GraphWalk.
     *
     * @see Router.addOrder в нём есть вариант,
     *  когда грузовик наберёт себе заказов на всю карту
     *  и будет возить, всё время продлевая свой путь,
     *  всё время проезжая по одним и тем же дорогам
     */
    fun concat(
        path1: GraphPath<Point, DefaultEdge>,
        path2: GraphPath<Point, DefaultEdge>
    ): GraphWalk<Point, DefaultEdge> {
        val walk1 = path1 as GraphWalk<Point, DefaultEdge> // cast
        val walk2 = path2 as GraphWalk<Point, DefaultEdge> // cast
        println("соединяю ${walk1.getStringForLog()} и ${walk2.getStringForLog()}")
        return walk1.concat(walk2) { walk1.weight + walk2.weight }
    }

    fun deletePoint(
        path: GraphPath<Point, DefaultEdge>,
        point: Point
    ): GraphPath<Point, DefaultEdge> {
        // если удалять конечную точку,
        // возвращать пустой путь
        if (path.endVertex == point)
            return GraphWalk.singletonWalk(InformationHolder.graph, point)

        val vertexList = path.vertexList
        vertexList.forEach {
            if (it == point)
                return@forEach

            vertexList.remove(it)
        }

        val weight = vertexList.count().toDouble()

        return build(vertexList, weight)
    }

    fun build(vertexList: List<Point>, weight: Double): GraphWalk<Point, DefaultEdge> =
        GraphWalk<Point, DefaultEdge>(
            InformationHolder.graph,
            vertexList,
            weight
        )
}