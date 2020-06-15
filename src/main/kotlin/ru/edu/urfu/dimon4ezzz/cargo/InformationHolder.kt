package ru.edu.urfu.dimon4ezzz.cargo

import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Хранитель данных.
 *
 * Является Singleton объектом.
 */
object InformationHolder {
    var points: List<Point> = CopyOnWriteArrayList()
    var graph = SimpleGraph<Point, DefaultEdge>(DefaultEdge::class.java)

    /**
     * Передаёт случайную точку в модели.
     *
     * @throws NoSuchElementException когда список пунктов не задан.
     */
    fun getRandomPoint(): Point =
        points.random()

    fun getPath(location: Point, destination: Point): GraphPath<Point, DefaultEdge> =
        DijkstraShortestPath.findPathBetween(graph, location, destination)
}