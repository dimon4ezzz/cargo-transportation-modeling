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

    /**
     * Из первого вычитает второй.
     *
     * // TODO сделать правильное вычитание путей
     */
    fun subtract(
        path1: GraphPath<Point, DefaultEdge>,
        path2: GraphPath<Point, DefaultEdge>
    ): GraphWalk<Point, DefaultEdge> {
        val edgeList = path1.edgeList.subtract(path2.edgeList).toList()
        val path = build(path2.startVertex, path2.endVertex, edgeList, path1.weight - path2.weight)
        println("вычитаю из ${path1.getStringForLog()} ${path2.getStringForLog()} и получаю ${path.getStringForLog()}")
        return path
    }

    /**
     * Сокращает весь путь до «начальная точка» – «конечная точка»,
     * забывает обо всех средних точках; это т. н. транзит.
     *
     * TODO не нужно, удалить
     */
    fun shortIt(path: GraphPath<Point, DefaultEdge>): GraphWalk<Point, DefaultEdge> =
        GraphWalk<Point, DefaultEdge>(
            InformationHolder.graph,
            listOf(path.startVertex, path.endVertex),
            path.weight
        )

    fun build(vertexList: List<Point>, weight: Double): GraphWalk<Point, DefaultEdge> =
        GraphWalk<Point, DefaultEdge>(
            InformationHolder.graph,
            vertexList,
            weight
        )

    fun build(start: Point, end: Point, edgeList: List<DefaultEdge>, weight: Double): GraphWalk<Point, DefaultEdge> =
        GraphWalk<Point, DefaultEdge>(
            InformationHolder.graph,
            start,
            end,
            edgeList,
            weight
        )
}