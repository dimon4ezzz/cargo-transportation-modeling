package ru.edu.urfu.dimon4ezzz.cargo.models

import ru.edu.urfu.dimon4ezzz.cargo.OrderSource

/**
 * Транспортный пункт.
 */
data class Point (
    /**
     * Название пункта.
     */
    val name: String
) {
    var orderSource: OrderSource? = null
}