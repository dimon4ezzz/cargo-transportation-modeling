package ru.edu.urfu.dimon4ezzz.cargo.models

import org.jgrapht.GraphPath
import org.jgrapht.graph.DefaultEdge

/**
 * Заказ.
 */
data class Order(
    /**
     * Название заказа.
     */
    var name: String,

    /**
     * Пункт доставки.
     */
    val destination: Point,

    /**
     * Просчитанный путь до точки назначения
     */
    var path: GraphPath<Point, DefaultEdge>
)