package ru.edu.urfu.dimon4ezzz.cargo.models

/**
 * Контейнер с заказами.
 */
data class Container (
    /**
     * Название контейнера.
     */
    val name: String,

    /**
     * Список заказов внутри.
     */
    val orders: ArrayList<Order>
) {
    private val capacity = 2

    fun add(order: Order): Boolean {
        if (orders.count() < capacity) {
            return orders.add(order)
        }

        return false
    }
}