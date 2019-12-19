package ru.edu.urfu.dimon4ezzz.cargo

import org.jgrapht.GraphPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.GraphWalk
import ru.edu.urfu.dimon4ezzz.cargo.models.Point

object GraphWalkBuilder {
    fun concat(path1: GraphPath<Point, DefaultEdge>, path2: GraphPath<Point, DefaultEdge>): GraphWalk<Point, DefaultEdge> {
        val walk1 = convert(path1)
        val walk2 = convert(path2)
        return walk1.concat(walk2) { walk1.weight + walk2.weight }
    }

    private fun convert(path: GraphPath<Point, DefaultEdge>) =
        GraphWalk<Point, DefaultEdge>(
            InformationHolder.graph,
            path.startVertex,
            path.endVertex,
            path.vertexList,
            path.edgeList,
            path.weight
        )
}