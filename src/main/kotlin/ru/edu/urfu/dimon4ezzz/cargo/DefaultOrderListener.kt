package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Order
import ru.edu.urfu.dimon4ezzz.cargo.models.Point

class DefaultOrderListener(
    private val point: Point
) : OrderListener {
    override fun onCreate(order: Order) {
        point.orderQueue.add(order)
        println("in ${point.name} queue incremented with ${order.name}")
    }
}