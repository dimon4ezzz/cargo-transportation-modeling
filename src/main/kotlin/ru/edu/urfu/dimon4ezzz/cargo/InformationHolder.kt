package ru.edu.urfu.dimon4ezzz.cargo

import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import ru.edu.urfu.dimon4ezzz.cargo.models.Point

/**
 * Хранитель данных.
 *
 * Является Singleton объектом.
 */
object InformationHolder {
    var points: List<Point>? = null
    var graph = SimpleGraph<Point, DefaultEdge>(DefaultEdge::class.java)

    /**
     * Передаёт случайную точку в модели.
     *
     * @throws IllegalStateException когда список пунктов не задан.
     */
    fun getRandomPoint(): Point {
        points
            ?.let { return it.random() }
            ?: throw IllegalStateException("list of points did not set")
    }

    fun getPath(location: Point, destination: Point): GraphPath<Point, DefaultEdge> =
        DijkstraShortestPath.findPathBetween(graph, location, destination)
}