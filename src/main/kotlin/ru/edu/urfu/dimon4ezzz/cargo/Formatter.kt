package ru.edu.urfu.dimon4ezzz.cargo

import org.jgrapht.GraphPath
import org.jgrapht.graph.DefaultEdge
import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import java.util.*

private const val DELIMITER = "-"

/**
 * Создаёт строку из списка вершин этого пути на графе.
 */
fun GraphPath<Point, DefaultEdge>.getStringForLog(): String {
    val joiner = StringJoiner(DELIMITER)
    vertexList.forEach {
        joiner.add(it.name)
    }

    return joiner.toString()
}