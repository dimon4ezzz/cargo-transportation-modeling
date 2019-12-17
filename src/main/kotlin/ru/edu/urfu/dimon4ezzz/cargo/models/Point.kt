package ru.edu.urfu.dimon4ezzz.cargo.models

import ru.edu.urfu.dimon4ezzz.cargo.DefaultOrderListener
import ru.edu.urfu.dimon4ezzz.cargo.OrderSource
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Транспортный пункт.
 */
data class Point (
    /**
     * Название пункта.
     */
    val name: String
) {
    private val defaultOrderListener = DefaultOrderListener(this)
    val orderQueue = ConcurrentLinkedQueue<Order>()

    var orderSource: OrderSource? = null

    fun setDefaultOrderSourceListener() {
        orderSource?.setOrderListener(defaultOrderListener)
    }
}